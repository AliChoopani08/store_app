package com.Ali.Store.App.product;

import com.Ali.Store.App.dto.product.request.SearchProductRequest;
import com.Ali.Store.App.dto.product.response.ProductSummary;
import com.Ali.Store.App.entities.productAndCategory.Category;
import com.Ali.Store.App.entities.productAndCategory.Product;
import com.Ali.Store.App.repository.productAndCategory.CategoryRepository;
import com.Ali.Store.App.repository.productAndCategory.ProductRepository;
import com.Ali.Store.App.service.product.ProductServiceImpl;
import com.Ali.Store.App.testConfigs.TestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.springframework.data.domain.PageRequest.of;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestConfig.class)
public class SpecificationProductTest {

    @Autowired
    private ProductRepository repository;
    @Autowired
    private CategoryRepository repositoryCategory;
    @Autowired
    private ProductServiceImpl service;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        repositoryCategory.deleteAll();

        Category digitalCategory = Category.builder()
                .name("Digital")
                .build();
        Product product1 = Product.builder()
                .name("S24 Ultra")
                .price(new BigDecimal("5000"))
                .isAvailable(true)
                .quantity(5)
                .slug("1-s24-ultra")
                .build();

        digitalCategory.addProduct(product1);

        repositoryCategory.save(digitalCategory);
    }

    @Test
    void shouldFindProduct_whenItExitsInDigitalCategory() {
        SearchProductRequest searchRequest = SearchProductRequest.builder()
                .category("digital")
                .build();
        Pageable page = of(0, 5);

        final Page<ProductSummary> responses = service.searchProducts(searchRequest, page);

        assertThat(responses)
                .extracting(ProductSummary::name, p -> p.categoryResponse().name())
                .containsExactly(tuple("S24 Ultra", "Digital"));
    }

    @Test
    void shouldFindProduct_whenItHasPriceLessThan6000() {
        SearchProductRequest searchRequest = SearchProductRequest.builder()
                .maxPrice(new BigDecimal("6000"))
                .build();
        Pageable page = of(0, 5);

        final Page<ProductSummary> responses = service.searchProducts(searchRequest, page);

        assertThat(responses)
                .extracting(ProductSummary::name, p -> p.price().intValue())
                .containsExactly(tuple("S24 Ultra", 5000));
    }
}
