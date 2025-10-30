package com.Ali.Store.App;

import com.Ali.Store.App.dto.product.request.*;
import com.Ali.Store.App.entities.userAndProfileUser.Users;
import com.Ali.Store.App.security.jwt.JwtAuthServiceInterface;
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
public class E2ETestForProductAndCategory {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private JwtAuthServiceInterface jwtAuthService;

    private String jwtAdmin;

    @BeforeAll
    void setUp() throws Exception {
        jwtAdmin = registerAnAdminBeforeOperations();
        createSomeProductBeforeOperations(jwtAdmin);
    }

    @Test
    void create_or_update_product() throws Exception {
        final CreateProductRequest productRequest = new CreateProductRequest("Xiaomi not 10 plus", 3000, "mobile", 5);

        mockMvc.perform(post("/admin/product")
                        .header("Authorization", "Bearer " + jwtAdmin)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Product created successfully"));
    }

    @Test
    void increase_product_quantity() throws Exception {
        // Previous quantity = 3
        // Add 2 more items to stoke -> 3 + 2 = 5
        final QuantityIncreaseRequest increaseRequest = new QuantityIncreaseRequest(2);

        mockMvc.perform(patch("/admin/product/quantity/1")
                        .header("Authorization", "Bearer " + jwtAdmin)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(increaseRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Product quantity updated successfully."))
                .andExpect(jsonPath("$.data.quantity").value(5));
    }

    @Test
    void reset_price() throws Exception {
        // Previous Price = 24000
        final PriceDeltaRequest deltaRequest = new PriceDeltaRequest(30000);

        mockMvc.perform(patch("/admin/product/price/1")
                .header("Authorization", "Bearer " + jwtAdmin)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deltaRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("New price registered"))
                .andExpect(jsonPath("$.data.price").value(30000));
    }

    @Test
    void dynamic_search_product() throws Exception {
        mockMvc.perform(get("/product")
                .param("category", "mobile")
                .param("maxPrice", "35000")
                .param("minPrice", "30000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].throw_exception_when_the_refresh_token_is_active_when_user_wants_to_login").value("Iphone 15 pro max"));
    }

    @Test
    void delete_product() throws Exception {
        mockMvc.perform(delete("/admin/product/1")
                .header("Authorization", "Bearer " + jwtAdmin))
                .andExpect(status().isNoContent());
    }

    private void createSomeProductBeforeOperations(String jwtAdmin) throws Exception {
        createACategory(jwtAdmin, "Mobile");

        helperCreatingProduct("S24 Ultra samsung", 24000, 3, jwtAdmin);

        helperCreatingProduct("Iphone 15 pro max", 32000, 5, jwtAdmin);
    }



    private void createACategory(String jwtAdmin, String categoryName) throws Exception {
        final CategoryRequest createCategory = new CategoryRequest(categoryName);

        mockMvc.perform(post("/admin/category")
                .header("Authorization", "Bearer " + jwtAdmin)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createCategory)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Category created successfully"));
    }

    private void helperCreatingProduct(String name, int price, int quantity, String jwtAdmin) throws Exception {

        final CreateProductRequest createRequest2 = new CreateProductRequest(name, price, "mobile", quantity);
        mockMvc.perform(post("/admin/product")
                        .header("Authorization", "Bearer " + jwtAdmin)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest2)))
                .andExpect(status().isCreated());
    }

    private String registerAnAdminBeforeOperations() {
        final Users admin2 = new Users();

        admin2.setUsername("09123456789");
        admin2.setPassword(encoder.encode("JAhs544@"));
        admin2.setRole(ROLE_ADMIN);

        return jwtAuthService.generateAccessToken(admin2.getUsername());

    }

}
