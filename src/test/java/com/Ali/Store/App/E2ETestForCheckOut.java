package com.Ali.Store.App;

import com.Ali.Store.App.dto.checkout.request.AddToCartRequest;
import com.Ali.Store.App.dto.user.request.UserRequest;
import com.Ali.Store.App.entities.productAndCategory.Category;
import com.Ali.Store.App.entities.productAndCategory.Product;
import com.Ali.Store.App.repository.productAndCategory.RepositoryCategory;
import com.Ali.Store.App.repository.productAndCategory.RepositoryProduct;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * <p>
 *  In JUnit5 by default,<br> for every test, an instance of test class is created with @TestInstance(TestInstance.Lifecycle.METHOD).<br>
 *  Before All method must be static.<br>
 *       Benefit:<br>
 *      - All test are independent and from each other.
 * </p>
 *
 * <p>
 *  We can change this operation with @TestInstance(TestInstance.Lifecycle.PER_CLASS).<br>
 *  In this structure, for all test method, just one instance of test class is created, and it is used for all test.<br>
 *      Benefit:<br>
 *        - Before All and Before Each methods can can be non-static.<br>
 *      Disadvantages:<br>
 *        - Tests aren't independent and can have an impact on other tests.
 * </p>
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class E2ETestForCheckOut {

    @Autowired
    private RepositoryProduct repositoryProduct;
    @Autowired
    private RepositoryCategory repositoryCategory;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private String jwt;

    @BeforeAll
    void setUp() throws Exception {

        final UserRequest userRequest = new UserRequest("09112223344", "Password123", "acer-315-55kg");
        final String registerResponse = mockMvc.perform(post("/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
         jwt = objectMapper.readTree(registerResponse).get("Access Token").asText();

        final Category mobile = new Category("Mobile");
        repositoryCategory.save(mobile);

        List<Product> products = List.of(new Product("Note 13 Pro Plus", 4000, 8, true, "note-13-pro-plus"),
                new Product("Huawei Honor 15c", 3500, 5, true, "huawei-honor-15-c"));

        products.forEach(mobile::addProduct);
        repositoryProduct.saveAll(products);
    }

    @Test
    void add_to_cart() throws Exception {
        final AddToCartRequest addToCartRequest = new AddToCartRequest(2L, 1);

        mockMvc.perform(post("/check-out/cart")
                        .header("Authorization", "Bearer " + jwt)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addToCartRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("A new cart item created successfully."));
    }

    @Test
    void place_an_order() throws Exception {
        final AddToCartRequest addToCartRequest = new AddToCartRequest(1L, 3);

        mockMvc.perform(post("/check-out/cart")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addToCartRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("A new cart item created successfully."));

        mockMvc.perform(post("/check-out/order")
                .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("This order placed successfully."));
    }
}

