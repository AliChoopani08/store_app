package com.Ali.Store.App.product.repository;

import com.Ali.Store.App.dto.product.response.ProductDtoForCartItems;
import com.Ali.Store.App.entities.productAndCategory.Category;
import com.Ali.Store.App.entities.productAndCategory.Product;
import com.Ali.Store.App.repository.productAndCategory.CategoryRepository;
import com.Ali.Store.App.repository.productAndCategory.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static java.util.List.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@DataJpaTest
@ActiveProfiles("test")
public class RepositoryProductTest {

    @Autowired
    private ProductRepository repositoryProduct;
    @Autowired
    private CategoryRepository repositoryCategory;


    private Category categoryFood;
    private Product product;

    @BeforeEach
    void setUp() {
        Category food = Category.builder()
                .name("Food")
                .build();

        product = Product.builder()
                .name("Sesame Oil")
                .price(new BigDecimal("2000"))
                .quantity(10)
                .isAvailable(true)
                .build();

        food.addProduct(product);
        categoryFood = repositoryCategory.save(food);
    }

    @Test
    void shouldFindProduct_byCategoryId_whenCategoryExists() {
        final Long categoryId = categoryFood.getId();
        Pageable pageable = PageRequest.of(0, 2);

        final Page<Product> productsCategory = repositoryProduct.findByCategoryId(categoryId, pageable);

        assertThat(productsCategory)
                .extracting(Product::getName, p -> p.getPrice().intValue())
                .containsExactly(tuple("Sesame Oil", 2000));
    }

    @Test
    void shouldFindProduct_byCategoryNameAndProductName_whenCategoryAndProductExist() {
        final String productName = product.getName();
        final String categoryName = categoryFood.getName();

        final Optional<Product> foundProduct = repositoryProduct.findByNameAndCategoryIgnoreCase(productName, categoryName);

        foundProduct.ifPresent(product -> assertThat(product)
                .extracting(Product::getName, p -> p.getPrice().intValue())
                .containsExactly("Sesame Oil", 2000));

    }

    @Test
    void shouldGetProductDetailsForCartItem_byProductIds_whenProductsExist() {
        final Long productId = product.getId();

        final List<ProductDtoForCartItems> mappedProduct = repositoryProduct.productDetailForCartItems(of(productId));

        assertThat(mappedProduct)
                .extracting(ProductDtoForCartItems::name)
                .containsExactly("Sesame Oil");
    }
    }
