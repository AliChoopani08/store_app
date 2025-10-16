package com.Ali.Store.App;

import com.Ali.Store.App.dto.product.request.CreateProductRequest;
import com.Ali.Store.App.dto.product.request.PriceDeltaRequest;
import com.Ali.Store.App.dto.product.request.QuantityIncreaseRequest;
import com.Ali.Store.App.dto.user.request.UserRequest;
import com.Ali.Store.App.entities.productAndCategory.Category;
import com.Ali.Store.App.entities.productAndCategory.Product;
import com.Ali.Store.App.entities.userAndProfileUser.RefreshToken;
import com.Ali.Store.App.entities.userAndProfileUser.Users;
import com.Ali.Store.App.repository.productAndCategory.RepositoryCategory;
import com.Ali.Store.App.repository.productAndCategory.RepositoryProduct;
import com.Ali.Store.App.repository.userAndProfileUser.RepositoryUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import static com.Ali.Store.App.entities.userAndProfileUser.Role.ROLE_ADMIN;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(PER_CLASS)
public class E2ETestForProductAndCategory {

    @Autowired
    private RepositoryProduct repositoryProduct;
    @Autowired
    private RepositoryCategory repositoryCategory;
    @Autowired
    private RepositoryUser repositoryUser;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private String jwt;

    @BeforeAll
    void setUp() { // add some product and category for test methods to database

        Category electronics = new Category("Electronics");
        repositoryCategory.save(electronics);
        Category food = new Category("Food");
        repositoryCategory.save(food);

        Product samsung_laptop = new Product("Samsung Laptop", 2000,5, true, "1-samsung-laptop");
        electronics.addProduct(samsung_laptop);
        repositoryProduct.save(samsung_laptop);
        final Product xiaomi_headphone = new Product("Xiaomi Headphone", 5000, 12, true, "2-xiaomi-headphone");
        electronics.addProduct(xiaomi_headphone);
        repositoryProduct.save(xiaomi_headphone);
        Product macaroni = new Product("Macaroni", 1000, 4, true, "macaroni");
        food.addProduct(macaroni);
        repositoryProduct.save(macaroni);
    }

    @BeforeAll
    void add_admin_user_before_operations() throws Exception {
        final Users user = new Users("09112223344", passwordEncoder.encode("Pasww12ord"), ROLE_ADMIN);

        final RefreshToken refreshToken = new RefreshToken();
        refreshToken.setDeviceId("acer315-55kg");
        String fakeRefreshToken = "Jsijhdhdgggygeggjjgdygyegy535TWFW667=";
        refreshToken.setToken(fakeRefreshToken);
        user.addRefreshToken(refreshToken);

        repositoryUser.save(user);

        final UserRequest loginUserRequest = new UserRequest("09112223344", "Pasww12ord", "acer315-55kg");
        final String loginResponseApi = mockMvc.perform(post("/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginUserRequest)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        jwt = objectMapper.readTree(loginResponseApi)
                .get("Access Token")
                .asText();
    }

    @Test
    void create_or_update_product() throws Exception { // ->  test update product
        final CreateProductRequest productRequest = new CreateProductRequest("Samsung Laptop", 2500, "Electronics", 7);

        mockMvc.perform(post("/admin/product")
                        .header("Authorization", "Bearer " + jwt)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productRequest)))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.product.name").value("Samsung Laptop"))
                .andExpect(jsonPath("$.data.product.price").value(2500))
                .andExpect(jsonPath("$.data.product.quantity").value(7))
                .andExpect(jsonPath("$.data.product.Category.name").value("Electronics"));
    }

    @Test
    void increase_quantity() throws Exception {
        final QuantityIncreaseRequest increaseQuantityRequest = new QuantityIncreaseRequest(3);

        mockMvc.perform(patch("/admin/product/quantity/3")
                        .header("Authorization", "Bearer " + jwt)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(increaseQuantityRequest)))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Macaroni"))
                .andExpect(jsonPath("$.data.quantity").value(7))
                .andExpect(jsonPath("$.data.Category.name").value("Food"));
    }

    @Test
    void dynamic_search_product() throws Exception {

        mockMvc.perform(get("/product")
                .param("category", "Elec")
                .param("maxPrice", "6000")
                .param("minPrice", "4000")
                .param("status", "true")
                .param("page", "0")
                .param("size", "5"))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].name").value("Xiaomi Headphone"))
                .andExpect(jsonPath("$.data.content[0].Category.name").value("Electronics"));
    }

    @Test
    void update_price() throws Exception {
        // old price -> 1000
        final PriceDeltaRequest priceDeltaRequest = new PriceDeltaRequest(2500);

        mockMvc.perform(patch("/admin/product/price/3")
                        .header("Authorization", "Bearer " + jwt)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(priceDeltaRequest)))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Macaroni"))
                .andExpect(jsonPath("$.data.Category.name").value("Food"))
                .andExpect(jsonPath("$.data.price").value(2500));
    }
}
