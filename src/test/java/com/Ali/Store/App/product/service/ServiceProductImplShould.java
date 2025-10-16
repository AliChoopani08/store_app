package com.Ali.Store.App.product.service;

import com.Ali.Store.App.dto.product.*;
import com.Ali.Store.App.dto.product.request.CreateProductRequest;
import com.Ali.Store.App.dto.product.request.PriceDeltaRequest;
import com.Ali.Store.App.dto.product.request.QuantityIncreaseRequest;
import com.Ali.Store.App.dto.product.request.SearchProductRequest;
import com.Ali.Store.App.dto.product.response.CategoryResponse;
import com.Ali.Store.App.dto.product.response.ProductResponse;
import com.Ali.Store.App.entities.productAndCategory.Product;
import com.Ali.Store.App.repository.productAndCategory.RepositoryProduct;
import com.Ali.Store.App.service.product.ServiceProductImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import java.util.List;
import java.util.Map;
import static com.Ali.Store.App.service.product.ItemStatus.INCREASED;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ServiceProductImplShould {


    @Mock
    private RepositoryProduct repositoryProduct;
    @Mock
    private ProductMapper productMapper;
    @InjectMocks
    private ServiceProductImpl serviceProduct;

    private CreateProductRequest productRequest;
    private Product product;
    private ProductResponse productResponse;
    private Product oldProduct;

    @BeforeEach
    void setUp() {
        productRequest = new CreateProductRequest("Banana", 2000, "Food", 2);
        product = new Product(2L, "Banana", 2000,5, true, "2-banana");
        productResponse = new ProductResponse(2L,"Banana", 2000,5,"2-banana",new CategoryResponse(5L, "Food"));
        oldProduct = new Product(2L, "Banana", 2000,3, true, "2-banana");
    }

    @Test
    void create_or_update_product() {
        when(repositoryProduct.findByNameAndCategoryIgnoreCase(any(String.class), any(String.class)))
                .thenReturn(of(oldProduct));
        mockSaveProduct(product);
        mockMapperToProductResponse(productResponse);
        final Map<String, Object> savedProduct = serviceProduct.createOrUpdateProduct(productRequest);

        assertThat(savedProduct.get("status"))
                .isEqualTo(INCREASED);
        assertThat(savedProduct.get("product"))
                .isEqualTo(productResponse);
    }

    @Test
    void search_product() {
        Page<Product> mockPage = new PageImpl<>(List.of(product));
        SearchProductRequest searchProduct = new SearchProductRequest("Food", null, 2500, 1000, true);
        Pageable pageable = PageRequest.of(0, 10);

    when(repositoryProduct.findAll(any(Specification.class), any(Pageable.class)))
            .thenReturn(mockPage);
    mockMapperToProductResponse(productResponse);
        final Page<ProductResponse> searchedProduct = serviceProduct.searchProducts(searchProduct, pageable);

        assertThat(searchedProduct.getContent().getFirst())
                .extracting(ProductResponse::name, ProductResponse::quantity)
                .containsExactly("Banana", 5);
    }

    @Test
    void update_product_price() {
        PriceDeltaRequest priceDeltaRequest = new PriceDeltaRequest(5000);
        ProductResponse response = new ProductResponse(2L,"Banana",5000,3,"2-banana",new CategoryResponse(5L, "Food"));
        Product updatedProduct = new Product(2L,"Banana", 5000,3, true, "2-banana");

        when(repositoryProduct.findById(any(Long.class)))
                .thenReturn(ofNullable(product));
        mockSaveProduct(updatedProduct);
        mockMapperToProductResponse(response);
        final ProductResponse updatedProductPrice = serviceProduct.updateProductPrice(priceDeltaRequest, 2L);

        assertThat(updatedProductPrice)
                .extracting(ProductResponse::name, ProductResponse::price, p -> p.categoryResponse().name())
                .containsExactly("Banana", 5000, "Food");
    }

    @Test
    void increase_product_quantity() {
        QuantityIncreaseRequest increaseRequest = new QuantityIncreaseRequest(5);

        when(repositoryProduct.findById(any(Long.class)))
                .thenReturn(ofNullable(oldProduct));
        mockSaveProduct(product);
        mockMapperToProductResponse(productResponse);
        final ProductResponse productResponse1 = serviceProduct.increaseQuality(increaseRequest, 2L);

        assertThat(productResponse1)
                .extracting(ProductResponse::name, ProductResponse::quantity)
                .containsExactly("Banana", 5);
    }

    @Test
    void create_slug_from_product_name() {
        Product product = new Product();
        product.setName("iPhone 13 Pro Max");

        final String productSlug = serviceProduct.createProductSlug(product);

        assertThat(productSlug).isEqualTo("iphone-13-pro-max");
    }


    private void mockSaveProduct(Product expectedProduct) {
        when(repositoryProduct.save(any(Product.class)))
                .thenReturn(expectedProduct);
    }

    private void mockMapperToProductResponse(ProductResponse response) {
        when(productMapper.productToProductResponse(any(Product.class)))
                .thenReturn(response);
    }
}
