package com.Ali.Store.App.integrationTests;

import com.Ali.Store.App.entities.productAndCategory.Category;
import com.Ali.Store.App.entities.productAndCategory.Product;
import com.Ali.Store.App.repository.productAndCategory.CategoryRepository;
import com.Ali.Store.App.testConfigs.TestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static java.util.List.of;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
public class ProductSearchIntegrationTest {

    @Autowired
    private CategoryRepository repositoryCategory;
    @Autowired
    private MockMvc mockMvc;


    @BeforeEach
    void setUp() {
        repositoryCategory.deleteAll();

        createDefaultCategoryAndProduct();
    }

    @Test
    void shouldSearchDynamicProduct() throws Exception {
        System.out.println(mockMvc.perform(get("/product")
                        .param("category", "book"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].name").value("Learning Java And Kotlin Concepts"))
                .andReturn()
                .getResponse().getContentAsString());
    }

    private void createDefaultCategoryAndProduct() {
        final Category mobile = Category.builder()
                .name("Mobile")
                .build();
        final Product product1 = Product.builder()
                .name("Iphone 15 pro max")
                .price(new BigDecimal("3000"))
                .isAvailable(true)
                .quantity(5)
                .build();

        mobile.addProduct(product1);

        Category book = Category.builder()
                .name("Book")
                .build();
        Product product2 = Product.builder()
                .name("Learning Java And Kotlin Concepts")
                .price(new BigDecimal("5000"))
                .isAvailable(true)
                .quantity(3)
                .build();
        book.addProduct(product2);

        repositoryCategory.saveAll(of(mobile, book));
    }
}
