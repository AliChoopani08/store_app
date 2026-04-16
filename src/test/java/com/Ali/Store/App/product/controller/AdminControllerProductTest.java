package com.Ali.Store.App.product.controller;

import com.Ali.Store.App.controller.product.admin.AdminControllerProduct;
import com.Ali.Store.App.dto.product.request.CreateProductRequest;
import com.Ali.Store.App.dto.product.request.PriceDeltaRequest;
import com.Ali.Store.App.dto.product.request.QuantityIncreaseRequest;
import com.Ali.Store.App.dto.product.response.CategorySummary;
import com.Ali.Store.App.dto.product.response.ProductSummary;
import com.Ali.Store.App.security.userDetails.UserDetailsImpl;
import com.Ali.Store.App.service.product.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static com.Ali.Store.App.testHelpers.GivenHelper.givenHelper;
import static com.Ali.Store.App.entities.userAndProfileUser.Role.ROLE_ADMIN;
import static com.Ali.Store.App.service.product.ItemStatus.INCREASED;
import static java.util.List.of;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
public class AdminControllerProductTest {

    @Autowired
    private ProductService serviceProduct;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private ProductSummary productResponse;
    UserDetailsImpl fakeUser;

    @BeforeEach
    void creation_common_objects() {
        productResponse = ProductSummary.builder()
                .id(1L)
                .name("Mint Yogurt")
                .price(new BigDecimal("2000"))
                .quantity(5)
                .categoryResponse(new CategorySummary(2L, "Food"))
                .build();

        fakeUser = UserDetailsImpl.builder()
                .username("09112223344")
                .password("Password123")
                .authorities(of(new SimpleGrantedAuthority(ROLE_ADMIN.name())))
                .build();
    }

    @Test
    void shouldCreateProduct_ifDoesNotExist() throws Exception {
        final CreateProductRequest productRequest = CreateProductRequest.builder()
                .name("Mint Yogurt")
                .price(new BigDecimal("2000"))
                .category("Food")
                .quantity(5)
                .build();
        Map<String, Object> responseMethod = new HashMap<>();
        responseMethod.put("status", INCREASED);
        responseMethod.put("product", productResponse);

        givenHelper(() -> serviceProduct.createOrUpdateProduct(any(CreateProductRequest.class)), responseMethod);

        mockMvc.perform(post("/admin/product")
                        .with(user(fakeUser))
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Product quantity and productPrice updated successfully"))
                .andExpect(jsonPath("$.data.product.name").value("Mint Yogurt"))
                .andExpect(jsonPath("$.data.product.quantity").value(5));
    }

    @Test
    void shouldIncreaseQuantity_whenProductExists() throws Exception {
        final QuantityIncreaseRequest increaseRequest = QuantityIncreaseRequest.builder()
                .quantity(3)
                .build();

        givenHelper(() -> serviceProduct.increaseQuality(any(QuantityIncreaseRequest.class), anyLong()), productResponse);

        mockMvc.perform(patch("/admin/product/quantity/1")
                        .with(user(fakeUser))
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(increaseRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Product quantity updated successfully"))
                .andExpect(jsonPath("$.data.name").value("Mint Yogurt"))
                .andExpect(jsonPath("$.data.quantity").value(5));
    }

    @Test
    void update_price() throws Exception {
        final PriceDeltaRequest priceDeltaRequest = PriceDeltaRequest.builder()
                .newPrice(new BigDecimal("5000"))
                .build();
        ProductSummary updatedProduct = productResponse.toBuilder()
                .price(new BigDecimal("5000"))
                .build();

        givenHelper(() -> serviceProduct.resetProductPrice(any(PriceDeltaRequest.class), anyLong()), updatedProduct);

        mockMvc.perform(patch("/admin/product/productPrice/1")
                        .with(user(fakeUser))
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(priceDeltaRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("New productPrice registered"))
                .andExpect(jsonPath("$.data.price").value(5000));
    }
}
