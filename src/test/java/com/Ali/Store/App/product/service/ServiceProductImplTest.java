package com.Ali.Store.App.product.service;

import com.Ali.Store.App.dto.product.*;
import com.Ali.Store.App.dto.product.request.CreateProductRequest;
import com.Ali.Store.App.dto.product.request.PriceDeltaRequest;
import com.Ali.Store.App.dto.product.request.QuantityIncreaseRequest;
import com.Ali.Store.App.dto.product.request.SearchProductRequest;
import com.Ali.Store.App.dto.product.response.CategorySummary;
import com.Ali.Store.App.dto.product.response.ProductSummary;
import com.Ali.Store.App.entities.productAndCategory.Category;
import com.Ali.Store.App.entities.productAndCategory.Product;
import com.Ali.Store.App.repository.productAndCategory.CategoryRepository;
import com.Ali.Store.App.repository.productAndCategory.ProductRepository;
import com.Ali.Store.App.service.product.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.Ali.Store.App.testHelpers.WhenHelper.whenHelper;
import static com.Ali.Store.App.service.product.ItemStatus.CREATED;
import static java.util.Optional.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.data.domain.PageRequest.of;

@ExtendWith(MockitoExtension.class)
public class ServiceProductImplTest {


    @Mock
    private ProductRepository repositoryProduct;
    @Mock
    private ProductMapper productMapper;
    @Mock
    private CategoryRepository repositoryCategory;
    @InjectMocks
    private ProductServiceImpl serviceProduct;

    private CreateProductRequest productRequest;
    private Product product;
    private ProductSummary productResponse;
    private Category category;

    @BeforeEach
    void setUp() {
        productRequest = getCreateProductRequest();

        category = Category.builder()
                .id(1L)
                .name("Fruit")
                .build();
        product = createProduct();
        category.addProduct(product);

        productResponse = createProductResponse();
    }

    @Test
    void shouldCreateProduct_whenProductDoesNotExist() {
        whenHelper(repositoryProduct.findByNameAndCategoryIgnoreCase(anyString(), anyString()), empty());
        whenHelper(repositoryCategory.findByNameIgnoreCase(anyString()), Optional.of(category));
        whenHelper(productMapper.toEntity(any(CreateProductRequest.class)), product);
        whenHelper(repositoryProduct.save(any(Product.class)), product);
        whenHelper(productMapper.toSummary(any(Product.class)), productResponse);

        final Map<String, Object> savedProduct = serviceProduct.createOrUpdateProduct(productRequest);

        assertThat(savedProduct.get("status"))
                .isEqualTo(CREATED);
        assertThat(savedProduct.get("product"))
                .isEqualTo(productResponse);
    }

    @Test
    void shouldFindProduct_bySearchOnCategoryAndMaxPrice_whenProductBeInFruitAndLessThan3000Price() {
        Page<Product> mockPage = new PageImpl<>(List.of(product));
        SearchProductRequest searchProduct = SearchProductRequest.builder()
                .category("Fruit")
                .maxPrice(new BigDecimal("3000"))
                .build();
        Pageable pageable = of(0, 5);

        whenHelper(repositoryProduct.findAll(any(Specification.class), any(Pageable.class)), mockPage);
        whenHelper(productMapper.toSummary(any(Product.class)), productResponse);

        final Page<ProductSummary> searchedProduct = serviceProduct.searchProducts(searchProduct, pageable);

        assertThat(searchedProduct.getContent().getFirst())
                .extracting(ProductSummary::name, p -> p.categoryResponse().name())
                .containsExactly("Banana", "Fruit");
    }

    @Test
    void shouldUpdateProductPrice_whenProductExists() {
        final Long productId = product.getId();
        PriceDeltaRequest priceDeltaRequest = new PriceDeltaRequest(new BigDecimal("2500"));
        Product updatedProduct = product.toBuilder()
                .price(new BigDecimal("2500"))
                .build();
        ProductSummary updatedResponse = productResponse.toBuilder()
                .price(new BigDecimal("2500"))
                .build();

        whenHelper(repositoryProduct.findById(anyLong()), Optional.of(product));
        whenHelper(repositoryProduct.save(any(Product.class)), updatedProduct);
        whenHelper(productMapper.toSummary(any(Product.class)), updatedResponse);

        final ProductSummary updatedProductPrice = serviceProduct.resetProductPrice(priceDeltaRequest, productId);

        assertThat(updatedProductPrice)
                .extracting(ProductSummary::name, p -> p.price().intValue())
                .containsExactly("Banana", 2500);
    }

    @Test
    void shouldIncreaseProductQuantity_whenProductExists() {
        final Long productId = product.getId();
        QuantityIncreaseRequest increaseRequest = new QuantityIncreaseRequest(5);
        Product updatedProduct = product.toBuilder()
                .quantity(7) // 2 + 5
                .build();
        ProductSummary updatedResponse = productResponse.toBuilder()
                .quantity(7)
                .build();

        whenHelper(repositoryProduct.findById(anyLong()), Optional.of(product));
        whenHelper(repositoryProduct.save(any(Product.class)), updatedProduct);
        whenHelper(productMapper.toSummary(any(Product.class)), updatedResponse);

        final ProductSummary productResponse1 = serviceProduct.increaseQuality(increaseRequest, productId);

        assertThat(productResponse1)
                .extracting(ProductSummary::name, ProductSummary::quantity)
                .containsExactly("Banana", 7);
    }

    @Test
    void shouldCreateProductSlug() {
        final String productSlug = serviceProduct.createProductSlug(product);

        assertThat(productSlug).isEqualTo("1-banana");
    }

    private static ProductSummary createProductResponse() {
        return ProductSummary.builder()
                .id(1L)
                .name("Banana")
                .price(new BigDecimal("2000"))
                .categoryResponse(new CategorySummary(1L, "Fruit"))
                .quantity(2)
                .build();
    }

    private static Product createProduct() {
        return Product.builder()
                .id(1L)
                .name("Banana")
                .price(new BigDecimal("2000"))
                .quantity(2)
                .build();
    }

    private static CreateProductRequest getCreateProductRequest() {
        return CreateProductRequest.builder()
                .name("Banana")
                .price(new BigDecimal("2000"))
                .category("Fruit")
                .quantity(2)
                .build();
    }
}
