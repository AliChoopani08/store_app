package com.Ali.Store.App.service.product;

import com.Ali.Store.App.dto.product.*;
import com.Ali.Store.App.dto.product.request.*;
import com.Ali.Store.App.dto.product.response.ProductSummary;
import com.Ali.Store.App.entities.productAndCategory.Category;
import com.Ali.Store.App.entities.productAndCategory.Product;
import com.Ali.Store.App.exceptions.productAndCategory.NotFoundCategory;
import com.Ali.Store.App.exceptions.productAndCategory.NotFoundProduct;
import com.Ali.Store.App.repository.productAndCategory.CategoryRepository;
import com.Ali.Store.App.repository.productAndCategory.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import static com.Ali.Store.App.service.product.ItemStatus.CREATED;
import static com.Ali.Store.App.service.product.ItemStatus.INCREASED;
import static com.Ali.Store.App.service.product.ProductSpecification.*;
import static java.util.Optional.ofNullable;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repositoryProduct;
    private final CategoryRepository repositoryCategory;
    private final ProductMapper productMapper;


    /**
     * Adds or updates a product
     *
     * <p>
     * Behavior:
     * - If the product exists in the database,
     * its quantity and productPrice is increased with
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
     * and productPrice was updated.
     * </p>
     *
     * @param productRequest CreateProductRequest
     *                       containing name, productPrice, category, quantity
     * @return Map with keys:
     * - "status": Item-status (CREATED or INCREASED)
     * - "product" ProductResponse
     */
    @Override
    @Transactional
    public Map<String, Object> createOrUpdateProduct(CreateProductRequest productRequest) {
        AtomicReference<ItemStatus> status = new AtomicReference<>();
        Map<String, Object> resultResponse = new HashMap<>();
        final ProductSummary productResponse = repositoryProduct.findByNameAndCategoryIgnoreCase(productRequest.getName(), productRequest.getCategory())

                .map(existingProduct -> { // if this product existed, update its quantity and productPrice.
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
    public ProductSummary increaseQuality(QuantityIncreaseRequest request, Long productId) {
        final Product foundProduct = getProductById(productId);

        final Product updatedProduct = foundProduct.toBuilder()
                .quantity(foundProduct.getQuantity() + request.getQuantity())
                .isAvailable(foundProduct.getQuantity() > 0)
                .build();

        final Product savedProduct = repositoryProduct.save(updatedProduct);

        log.info("Product [{}] quantity increased successfully", savedProduct.getId());
        return productMapper.toSummary(savedProduct);
    }

    @Override
    @Transactional
    public Page<ProductSummary> searchProducts(SearchProductRequest search, Pageable pageable) {
        List<Specification<Product>> specs = new ArrayList<>();

        ifFieldPresent(search.category(), c -> specs.add(withCategory(c)));
        ifFieldPresent(search.name(), n -> specs.add(withName(n)));
        ifFieldPresent(search.minPrice(), minPrice -> specs.add(withMinPrice(minPrice.intValue())));
        ifFieldPresent(search.maxPrice(), maxPrice -> specs.add(withMaxPrice(maxPrice.intValue())));
        ifFieldPresent(search.status(), statue -> specs.add(isAvailable(statue)));

        final Specification<Product> finalSpec = specs.stream()
                .reduce(new AlwaysTrueSpecification<>(), Specification::and);

        return repositoryProduct.findAll(finalSpec, pageable)
                .map(productMapper::toSummary);
    }

    private <T> void ifFieldPresent(T field, Consumer<T> addCondition) {
        ofNullable(field)
                .ifPresent(addCondition);
    }

    @Override
    @Transactional
    public ProductSummary updateProductPrice(PriceDeltaRequest desiredProduct, Long productId) {
        final Product foundProduct = getProductById(productId);

        foundProduct.setPrice(desiredProduct.getNewPrice());
        repositoryProduct.save(foundProduct);

        log.info("Product [{}] price updated successfully", foundProduct.getId());
        return productMapper.toSummary(foundProduct);
    }

    @Override
    public ProductSummary getProductByNameAndCategory(FindProductRequest findProductRequest) {
        final Product foundProduct = repositoryProduct.findByNameAndCategoryIgnoreCase(findProductRequest.getName(), findProductRequest.getCategory())
                .orElseThrow(() -> new NotFoundProduct(findProductRequest.getName()));

        return productMapper.toSummary(foundProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId) {
        final Product foundProduct = getProductById(productId);

        repositoryProduct.deleteById(foundProduct.getId());
        log.info("Product [{}] deleted successfully", foundProduct.getId());
    }

    private Category getCategoryByName(String name) {
        return repositoryCategory.findByNameIgnoreCase(name)
                .orElseThrow(() -> new NotFoundCategory(name));
    }

    private ProductSummary createNewProduct(CreateProductRequest productRequest) {
        final Category takenCategory = getCategoryByName(productRequest.getCategory());
        final Product product = productMapper.toEntity(productRequest);

        takenCategory.addProduct(product);
        product.setAvailable(true);
        final Product savedProduct = repositoryProduct.save(product);

        UnaryOperator<Product> createProductWithSlug = p -> p.toBuilder()
                .slug(createProductSlug(p))
                .build();
        final Product finalSavedProduct = repositoryProduct.save(createProductWithSlug.apply(savedProduct));

        log.info("A new product [{}] created successfully", finalSavedProduct.getId());
        return productMapper.toSummary(finalSavedProduct);
    }

    private ProductSummary updateProductPriceAndQuantity(CreateProductRequest productRequest, Product existingProduct) {
        existingProduct.setPrice(productRequest.getPrice());
        existingProduct.setQuantity(productRequest.getQuantity());
        final Product savedProduct = repositoryProduct.save(existingProduct);

        log.info("Product [{}] quantity and price increased successfully", savedProduct.getId());
        return productMapper.toSummary(savedProduct);

    }

    public String createProductSlug(Product product) {
        final Long productId = product.getId();
        final String[] separatedName = product.getName().split(" ");
        final List<String> nameList = Arrays.stream(separatedName)
                .toList();

        Function<String, String> deleteSpecificCharacter = n -> n.replaceAll("[^a-zA-Z0-9]", "");

        return nameList.stream()
                .map(deleteSpecificCharacter)
                .map(String::toLowerCase)
                .collect(Collectors.joining("-", productId + "-", ""));
    }

    private Product getProductById(Long productId) {
        return repositoryProduct.findById(productId)
                .orElseThrow(() -> new NotFoundProduct(productId));
    }
}
