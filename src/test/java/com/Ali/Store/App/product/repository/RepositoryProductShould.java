package com.Ali.Store.App.product.repository;

import com.Ali.Store.App.dto.product.response.ProductDtoForCartItems;
import com.Ali.Store.App.entities.productAndCategory.Category;
import com.Ali.Store.App.entities.productAndCategory.Product;
import com.Ali.Store.App.repository.productAndCategory.RepositoryCategory;
import com.Ali.Store.App.repository.productAndCategory.RepositoryProduct;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@DataJpaTest
public class RepositoryProductShould {

    @Autowired
    private RepositoryProduct repositoryProduct;
    @Autowired
    private RepositoryCategory repositoryCategory;


    private Category food;
    private Category digital;

    @BeforeEach
    void setUp() {
        food = new Category("Food");
        repositoryCategory.save(food);

        digital = new Category("Digital");
        repositoryCategory.save(digital);
    }

    @Test
    void get_books_by_category() {

        saveProducts(new Product("Sesame Oil", 2000, 10, true, "sesame-oil"), food);
        saveProducts(new Product("Macaroni", 3500, 7, true, "macaroni"), food);
        saveProducts(new Product("Oil", 4500, 7, true, "oil"), food);

        Pageable pageable = PageRequest.of(0, 2);
        final Page<Product> productsCategory = repositoryProduct.findByCategoryId(1L, pageable);

        assertThat(productsCategory)
                .extracting(Product::getName, Product::getPrice)
                .containsExactly(tuple("Sesame Oil", 2000)
                                      , tuple("Macaroni", 3500));
    }

    @Test
    void find_book_by_id() {
        saveProducts(new Product("Sesame Oil", 2000,10, true, "sesame-oil"), food);
        saveProducts(new Product("Macaroni", 3500, 7, true, "macaroni"), food);
        saveProducts(new Product("Mobile Samsung", 10000, 3, true, "mobile-sumsung") , digital);
        saveProducts(new Product("Laptop Lenovo", 20000, 5, true, "laptop-lenovo") , digital);

        final Optional<Product> foundById = repositoryProduct.findById(3L);

        assertThat(foundById.get())
                .extracting(Product::getName, Product::getPrice, p -> p.getCategory().getName())
                .containsExactly("Mobile Samsung", 10000, "Digital");

}

    @Test
    void find_by_name_and_category() {
        saveProducts(new Product("Sesame Oil", 2000,10, true, "sesame-oil"), food);
        saveProducts(new Product("Macaroni", 3500, 7, true, "macaroni"), food);
        saveProducts(new Product("Mobile Samsung", 10000, 3, true, "mobile-sumsung") , digital);

        final Optional<Product> foundProduct = repositoryProduct.findByNameAndCategoryIgnoreCase("Macaroni", "Food");

        Assertions.assertThat(foundProduct.get())
                .extracting(Product::getName, Product::getPrice)
                .containsExactly("Macaroni", 3500);
    }

    @Test
    void get_product_detail_for_cart_items() {
        saveProducts(new Product("Sesame Oil", 2000, 12, true, "sesame-oil"), food);
        saveProducts(new Product("Mobile Samsung", 10000, 23, true, "mobile-samsung"), digital);
        saveProducts(new Product("Macaroni", 3500, 3, true, "macaroni"), food);

        final List<ProductDtoForCartItems> mappedProduct = repositoryProduct.productDetailForCartItems(List.of(2L));

        final List<ProductDtoForCartItems> expectedDto = List.of(new ProductDtoForCartItems(2L, "Mobile Samsung", 10000, true));
        assertThat(mappedProduct)
                .isEqualTo(expectedDto);
    }

    private void saveProducts(Product product, Category category) {
        category.addProduct(product);
        repositoryProduct.save(product);
    }
    }
