package com.Ali.Store.App.integrationTests;

import com.Ali.Store.App.dto.product.request.*;
import com.Ali.Store.App.entities.productAndCategory.Category;
import com.Ali.Store.App.entities.productAndCategory.Product;
import com.Ali.Store.App.entities.userAndProfileUser.Users;
import com.Ali.Store.App.repository.productAndCategory.CategoryRepository;
import com.Ali.Store.App.repository.productAndCategory.ProductRepository;
import com.Ali.Store.App.security.jwt.JwtAuthServiceInterface;
import com.Ali.Store.App.testConfigs.TestJpaAuditingConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static com.Ali.Store.App.entities.userAndProfileUser.Role.ROLE_ADMIN;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestInstance(PER_CLASS)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestJpaAuditingConfig.class)
@Transactional
public class ProductAdminIntegrationTest {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTHORIZATION = "Authorization";
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private JwtAuthServiceInterface jwtAuthService;
    @Autowired
    private CategoryRepository repositoryCategory;
    @Autowired
    private ProductRepository repositoryProduct;

    private String adminAccessToken;

    @BeforeAll
    void setUp() {
        adminAccessToken = registerAnAdminBeforeOperations();
    }

    @Test
    void shouldCreateProduct_whenCategoryExistsAndProductDoesNotExist() throws Exception {
        final Category category = createCategory();
        final CreateProductRequest productRequest = CreateProductRequest.builder()
                .name("Iphone 15 pro max")
                .price(new BigDecimal("3000"))
                .category(category.getName())
                .quantity(5)
                .build();

        mockMvc.perform(post("/admin/product")
                        .header(AUTHORIZATION, BEARER_PREFIX + adminAccessToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Product created successfully"))
                .andExpect(jsonPath("$.data.product.name").value("Iphone 15 pro max"));
    }

    @Test
    void shouldIncreaseQuantity_whenProductExists() throws Exception {
        /*
        Previous quantity = 5
        Add 2 more items to stoke -> 5 + 2 = 7
         */
        createDefaultProduct();
        final Long productId = getProductId();
        final QuantityIncreaseRequest increaseRequest = new QuantityIncreaseRequest(2);

        mockMvc.perform(patch("/admin/product/quantity/" + productId)
                        .header(AUTHORIZATION, BEARER_PREFIX + adminAccessToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(increaseRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Product quantity updated successfully"))
                .andExpect(jsonPath("$.data.quantity").value(7));
    }

    @Test
    void shouldUpdateProductPrice_whenProductExists() throws Exception {
        createDefaultProduct();
        final Long productId = getProductId();
        final PriceDeltaRequest deltaRequest = new PriceDeltaRequest(new BigDecimal("30000"));

        mockMvc.perform(patch("/admin/product/productPrice/" + productId)
                        .header(AUTHORIZATION, BEARER_PREFIX + adminAccessToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deltaRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("New productPrice registered"))
                .andExpect(jsonPath("$.data.price").value(30000));
    }

    @Test
    void shouldDeleteProduct_whenProductExists() throws Exception {
        createDefaultProduct();
        final Long productId = getProductId();

        mockMvc.perform(delete("/admin/product/" + productId)
                        .header(AUTHORIZATION, BEARER_PREFIX + adminAccessToken))
                .andExpect(status().isNoContent());
    }

    private String registerAnAdminBeforeOperations() {
        final Users admin2 = new Users();

        admin2.setUsername("09123456789");
        admin2.setPassword(encoder.encode("JAhs544@"));
        admin2.setRole(ROLE_ADMIN);

        return jwtAuthService.generateAccessToken(admin2.getUsername());
    }

    private Category createCategory() {
        final Category category = Category.builder()
                .name("Mobile")
                .build();

        return repositoryCategory.save(category);
    }

    private void createDefaultProduct() {
        Category category = createCategory();

        Product product = Product.builder()
                .name("Iphone 15 pro max")
                .price(new BigDecimal("3000"))
                .quantity(5)
                .build();

        category.addProduct(product);
        repositoryCategory.save(category);
    }

    private Long getProductId() {
        return repositoryProduct
                .findAll()
                .getFirst()
                .getId();
    }


}
