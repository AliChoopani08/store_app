package com.Ali.Store.App.integrationTests;

import com.Ali.Store.App.dto.checkout.request.AddToCartRequest;
import com.Ali.Store.App.dto.user.request.RegisterUserRequest;
import com.Ali.Store.App.entities.productAndCategory.Category;
import com.Ali.Store.App.entities.productAndCategory.Product;
import com.Ali.Store.App.repository.productAndCategory.CategoryRepository;
import com.Ali.Store.App.repository.productAndCategory.ProductRepository;
import com.Ali.Store.App.repository.userAndProfileUser.UserRepository;
import com.Ali.Store.App.testConfigs.TestConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * <p>
 * In JUnit5 by default,<br> for every test, an instance of test class is created with @TestInstance(TestInstance.Lifecycle.METHOD).<br>
 * Before All method must be static.<br>
 * Benefit:<br>
 * - All test are independent of each other.
 * </p>
 *
 * <p>
 * We can change this operation with @TestInstance(TestInstance.Lifecycle.PER_CLASS).<br>
 * In this structure, for all test method, just one instance of test class is created, and it is used for all test.<br>
 * Benefit:<br>
 * - Before All and Before Each methods can can be non-static.<br>
 * Disadvantages:<br>
 * - Tests aren't independent and can have an impact on other tests.
 * </p>
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
public class CheckOutIntegrationTest {

    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Autowired
    private ProductRepository repositoryProduct;
    @Autowired
    private CategoryRepository repositoryCategory;
    @Autowired
    private UserRepository repositoryUser;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private String jwt;

    @BeforeEach
    void setUp() throws Exception {
        repositoryUser.deleteAll();
        repositoryCategory.deleteAll();

        final RegisterUserRequest userRequest = createUserRequest();
        final String registerResponse = createUserAndAccessToken(userRequest);
        jwt = objectMapper.readTree(registerResponse)
                .get("Access Token")
                .asText();

        final Category mobile = Category.builder()
                .name("Mobile")
                .build();
        Product product = Product.builder()
                .name("Note 13 Pro Plus")
                .price(new BigDecimal("2000"))
                .quantity(5)
                .isAvailable(true)
                .build();
        mobile.addProduct(product);

        repositoryCategory.save(mobile);
    }


    @Test
    void shouldAddProductToUserCart_whenUserHasLoggedInAndProductExists() throws Exception {
        final Long productId = getProductId();
        final AddToCartRequest req = new AddToCartRequest(productId, 1);

        mockMvc.perform(post("/check-out/cart")
                        .header(AUTHORIZATION, BEARER_PREFIX + jwt)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("A new cart item created successfully."))
                .andExpect(jsonPath("$.data.['User cart details'][0].['Product name']").value("Note 13 Pro Plus"))
                .andExpect(jsonPath("$.data.['User cart details'][0].Quantity").value(1));
    }


    @Test
    void shouldPlaceOrder_whenUserAndUserCartExist() throws Exception {
        final Long productId = getProductId();
        final AddToCartRequest addToCartRequest = new AddToCartRequest(productId, 3);

        mockMvc.perform(post("/check-out/cart")
                        .header(AUTHORIZATION, BEARER_PREFIX + jwt)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addToCartRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("A new cart item created successfully."));

        mockMvc.perform(post("/check-out/order")
                        .header(AUTHORIZATION, BEARER_PREFIX + jwt))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("This order placed successfully."))
                .andExpect(jsonPath("$.data.['Order items details'][0].['Product name']").value("Note 13 Pro Plus"))
                .andExpect(jsonPath("$.data.['Total prices']").value(new BigDecimal("6000.0")));
    }

    private static RegisterUserRequest createUserRequest() {
        return RegisterUserRequest.builder()
                .username("09876543210")
                .password("Password123")
                .build();
    }

    private String createUserAndAccessToken(RegisterUserRequest req) throws Exception {
        return mockMvc.perform(post("/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    private Long getProductId() {
        return repositoryProduct.findAll()
                .getFirst()
                .getId();
    }
}

