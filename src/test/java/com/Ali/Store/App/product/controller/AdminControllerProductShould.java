package com.Ali.Store.App.product.controller;

import com.Ali.Store.App.controller.product.admin.AdminControllerProduct;
import com.Ali.Store.App.dto.product.request.CreateProductRequest;
import com.Ali.Store.App.dto.product.request.PriceDeltaRequest;
import com.Ali.Store.App.dto.product.request.QuantityIncreaseRequest;
import com.Ali.Store.App.dto.product.response.CategoryResponse;
import com.Ali.Store.App.dto.product.response.ProductResponse;
import com.Ali.Store.App.entities.userAndProfileUser.ProfileUser;
import com.Ali.Store.App.security.userDetails.UserDetailsImpl;
import com.Ali.Store.App.service.product.ServiceProductInterface;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.Ali.Store.App.entities.userAndProfileUser.Role.ROLE_ADMIN;
import static com.Ali.Store.App.service.product.ItemStatus.INCREASED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminControllerProduct.class)
@Import(ConfigClassTest.class)
@ActiveProfiles("test")
public class AdminControllerProductShould {

    @Autowired
    private ServiceProductInterface serviceProduct;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private ProductResponse productResponse;
    UserDetailsImpl fakeUser;
    @BeforeEach
    void creation_common_objects() {
        productResponse =  new ProductResponse(3L, "Mint Yogurt", 2000, 5,"1-mint-yogurt",new CategoryResponse(2L, "Food"));

        final ProfileUser profileUser = new ProfileUser("Hoseyn", null, null, LocalDate.of(2003, 5, 21));
        fakeUser =
                new UserDetailsImpl("09112223344", "Password123", List.of(new SimpleGrantedAuthority(ROLE_ADMIN.name())), 2L, profileUser);
    }

    @Test
    void create_or_update_product() throws Exception {
        // Given
        final CreateProductRequest productRequest = new CreateProductRequest("Mint Yogurt", 2000, "Food", 3);
        Map<String, Object> responseMethod = new HashMap<>();
        responseMethod.put("status", INCREASED);
        responseMethod.put("product", productResponse);

        given(serviceProduct.createOrUpdateProduct(any(CreateProductRequest.class)))
                .willReturn(responseMethod);
        // When
        mockMvc.perform(post("/admin/product")
                        .with(user(fakeUser))
                        .with(csrf())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productRequest)))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Product quantity is updated"));
    }

    @Test
    void increase_quantity() throws Exception {
        final QuantityIncreaseRequest increaseRequest = new QuantityIncreaseRequest(3);

        given(serviceProduct.increaseQuality(any(QuantityIncreaseRequest.class), any(Long.class)))
                .willReturn(productResponse);

        mockMvc.perform(patch("/admin/product/quantity/3")
                        .with(user(fakeUser))
                        .with(csrf())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(increaseRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Product quantity is updated"))
                .andExpect(jsonPath("$.data.name").value("Mint Yogurt"))
                .andExpect(jsonPath("$.data.quantity").value(5));
    }

    @Test
    void update_price() throws Exception {
        final PriceDeltaRequest priceDeltaRequest = new PriceDeltaRequest(5000);
        ProductResponse expectedProductResponse = new ProductResponse(3L, "Mint Yogurt", 5000, 5,"1-mint-yogurt",new CategoryResponse(2L, "Food"));

        given(serviceProduct.updateProductPrice(any(PriceDeltaRequest.class), any(Long.class)))
                .willReturn(expectedProductResponse);

        mockMvc.perform(patch("/admin/product/price/3")
                        .with(user(fakeUser))
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(priceDeltaRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("New price registered"))
                .andExpect(jsonPath("$.data.price").value(5000));
    }
}
