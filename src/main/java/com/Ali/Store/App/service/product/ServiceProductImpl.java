package com.Ali.Store.App.service.product;

import com.Ali.Store.App.dto.product.*;
import com.Ali.Store.App.dto.product.request.*;
import com.Ali.Store.App.dto.product.response.ProductResponse;
import com.Ali.Store.App.entities.productAndCategory.Category;
import com.Ali.Store.App.entities.productAndCategory.Product;
import com.Ali.Store.App.exceptions.productAndCategory.NotFoundCategory;
import com.Ali.Store.App.exceptions.productAndCategory.NotFoundProduct;
import com.Ali.Store.App.repository.productAndCategory.RepositoryCategory;
import com.Ali.Store.App.repository.productAndCategory.RepositoryProduct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import static com.Ali.Store.App.service.product.ItemStatus.CREATED;
import static com.Ali.Store.App.service.product.ItemStatus.INCREASED;
import static com.Ali.Store.App.service.product.ProductSpecification.*;

@Service
@RequiredArgsConstructor
public class ServiceProductImpl implements ServiceProductInterface{

    private final RepositoryProduct repositoryProduct;
    private final RepositoryCategory repositoryCategory;
    private final ProductMapper productMapper;


    /**
     * Adds or updates a product
     *
     * <p>
     * Behavior:
     * - If the product exists in the database,
     * its quantity and price is increased with
     * fields of inserted product.
     * - If the product does not exist, a new
     * product is created.
     * </p>
     *
     * <p><b>Note for Frontend:</b>
     * - The response includes a status field that
     * indicates the operation:
     * - "CREATED": a new product was creates.
     * - "INCREASED": an existing product quantity
     * and price was updated.
     *</p>
     *
     *
     * @param productRequest CreateProductRequest
     *                       containing name, price, category, quantity
     * @return Map with keys:
     * - "status": Item-status (CREATED or INCREASED)
     * - "product" ProductResponse
     */
    @Override
    @Transactional
    public Map<String, Object> createOrUpdateProduct(CreateProductRequest productRequest) {
        AtomicReference<ItemStatus> status = new AtomicReference<>();
        Map<String, Object> resultResponse = new HashMap<>();
        final ProductResponse productResponse = repositoryProduct.findByNameAndCategoryIgnoreCase(productRequest.getName(), productRequest.getCategory())

                .map(existingProduct -> { // if this product existed, update its quantity and price.
                    status.set(INCREASED);
                            return updateProductPriceAndQuantity(productRequest, existingProduct);
                        }
                ).orElseGet(() -> { // if this product didn't exist, create a new product
                    status.set(CREATED);
                    return createNewProduct(productRequest);
                });
         resultResponse.put("status", status.get());
         resultResponse.put("product", productResponse);

         return resultResponse;
    }

    @Override
    @Transactional
    public ProductResponse increaseQuality(QuantityIncreaseRequest request, Long productId) {
        final Product foundProduct = getProductById(productId);

        return updateProductQuantity(request.getQuantity(), foundProduct);
    }

    @Override
    public Page<ProductResponse> searchProducts(SearchProductRequest search, Pageable pageable) {
        Specification<Product> spec = ProductSpecification.withCategory(search.category())
                .and(withName(search.name()))
                .and(withMaxPrice(search.maxPrice()))
                .and(withMinPrice(search.minPrice()))
                .and(isAvailable(search.status()));

        return repositoryProduct.findAll(spec, pageable)
                .map(productMapper::productToProductResponse);
    }

    @Override
    @Transactional
    public ProductResponse updateProductPrice(PriceDeltaRequest desiredProduct, Long productId) {
        final Product foundProduct = getProductById(productId);

        foundProduct.setPrice(desiredProduct.getNewPrice());
        repositoryProduct.save(foundProduct);

        return productMapper.productToProductResponse(foundProduct);
    }

    @Override
    public ProductResponse getProductByNameAndCategory(FindProductRequest findProductRequest) {
        final Product foundProduct = repositoryProduct.findByNameAndCategoryIgnoreCase(findProductRequest.getName(), findProductRequest.getCategory())
                .orElseThrow(() -> new NotFoundProduct("This product is not exist in database !"));

        return productMapper.productToProductResponse(foundProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId) {
        final Product foundProduct = getProductById(productId);

        repositoryProduct.delete(foundProduct);
    }

    private Category getCategoryByName(String name) {
        return repositoryCategory.findByNameIgnoreCase(name)
                .orElseThrow(() -> new NotFoundCategory("This category is not exist !" + " name: " + name));
    }

    private ProductResponse updateProductQuantity(int requestedQuantity, Product existingProduct) {
        existingProduct.setQuantity(existingProduct.getQuantity() + requestedQuantity);

        final Product savedProduct = repositoryProduct.save(existingProduct);

        return productMapper.productToProductResponse(savedProduct);
    }

    private ProductResponse createNewProduct(CreateProductRequest productRequest) {
        final Category takenCategory = getCategoryByName(productRequest.getCategory());
        final Product product = productMapper.productRequestToProduct(productRequest);

        takenCategory.addProduct(product);
        product.setSlug(createProductSlug(product));
        product.setAvailable(true);

        final Product savedProduct = repositoryProduct.save(product);

        return productMapper.productToProductResponse(savedProduct);
    }

    private ProductResponse updateProductPriceAndQuantity(CreateProductRequest productRequest,Product existingProduct) {
        existingProduct.setPrice(productRequest.getPrice());
        existingProduct.setQuantity(productRequest.getQuantity());
        final Product savedProduct = repositoryProduct.save(existingProduct);

        return productMapper.productToProductResponse(savedProduct);

    }

    public String createProductSlug(Product product) {
        final String[] separatedName = product.getName().split(" ");
        final List<String> nameList = Arrays.stream(separatedName)
                .toList();

        Function<String, String> deleteSpecificCharacter = n-> n.replaceAll("[^a-zA-Z0-9]", "");
        BinaryOperator<String> formatSlug = (slug,nameProduct) -> slug + "-" + nameProduct;

        final String createdSlug = nameList.stream()
                .map(deleteSpecificCharacter)
                .map(String::toLowerCase)
                .reduce("", formatSlug);

        return createdSlug.substring(1);
    }

    private Product getProductById(Long productId) {
        return repositoryProduct.findById(productId)
                .orElseThrow(() -> new NotFoundProduct("This product is nor exist in database !"));
    }
}
