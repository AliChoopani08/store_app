package com.Ali.Store.App.checkOut.controller;

import com.Ali.Store.App.controller.checkout.CheckoutController;
import com.Ali.Store.App.dto.checkout.request.AddToCartRequest;
import com.Ali.Store.App.dto.checkout.response.CartItemDto;
import com.Ali.Store.App.dto.checkout.response.OrderItemDetailsDto;
import com.Ali.Store.App.dto.checkout.response.UserCartDetailsDto;
import com.Ali.Store.App.dto.checkout.response.UserOrderDetailsDto;
import com.Ali.Store.App.entities.userAndProfileUser.ProfileUser;
import com.Ali.Store.App.security.userDetails.UserDetailsImpl;
import com.Ali.Store.App.service.checkOut.cart.CartService;
import com.Ali.Store.App.service.checkOut.order.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static com.Ali.Store.App.testHelpers.GivenHelper.givenHelper;
import static com.Ali.Store.App.service.checkOut.order.OrderStatus.PENDING;
import static com.Ali.Store.App.service.product.ItemStatus.CREATED;
import static java.time.LocalDate.of;
import static java.util.List.of;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CheckoutController.class)
@Import(ConfigTestCheckOutController.class)
@ActiveProfiles("test")
public class CheckoutControllerTest {

    @Autowired
    private OrderService serviceOrder;
    @Autowired
    private CartService serviceCart;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private UserDetailsImpl fakeUser;

    @BeforeEach
    void setUp() {
        ProfileUser profileUser = ProfileUser.builder()
                .name("Mohammad")
                .birthData(of(1992, 9, 12))
                .build();
        fakeUser = UserDetailsImpl.builder()
                .username("09112223344")
                .id(10L)
                .profileUser(profileUser)
                .build();
    }

    @Test
    void shouldAddToCart_whenProductExistsAndRequestBeValid() throws Exception {
        CartItemDto cartItemDto = CartItemDto.builder()
                .id(1L)
                .productName("Mint Yogurt")
                .productCategory("Food")
                .totalProductPrice(new BigDecimal("11500"))
                .quantity(5)
                .build();
        UserCartDetailsDto userCartDetails = UserCartDetailsDto.builder()
                .userid(10L)
                .cartItemsDto(of(cartItemDto))
                .build();
        final AddToCartRequest addToCartRequest = new AddToCartRequest(3L, 5);

        Map<String, Object> expectedResponseMap = new HashMap<>();
        expectedResponseMap.put("status", CREATED); // -> Assume this product does not exist in the cart
        expectedResponseMap.put("Cart details", userCartDetails);

        givenHelper(() -> serviceCart.addToCart(any(Long.class), any(AddToCartRequest.class)),
                expectedResponseMap);

        mockMvc.perform(post("/check-out/cart")
                        .with(user(fakeUser))
                        .with(csrf())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addToCartRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("A new cart item created successfully."));
    }

    @Test
    void shouldDeleteProductFromCart_whenProductExists() throws Exception {
        willDoNothing().given(serviceCart).reduceCartItemQuantity(anyLong(),anyInt(), anyLong());

        mockMvc.perform(delete("/check-out/cart/3/2")
                .with(user(fakeUser))
                .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldPlaceOrder_whenCartUserExists() throws Exception {
        OrderItemDetailsDto userOrderItemsDetails = OrderItemDetailsDto.builder()
                .id(1L)
                .productId(3L)
                .productName("Mint Yogurt")
                .productSlug("mint-yogurt")
                .productPrice(new BigDecimal("11500"))
                .quantity(5)
                .build();
        UserOrderDetailsDto expectedUserOrderDetails = UserOrderDetailsDto.builder()
                .userId(10L)
                .orderId(1L)
                .orderItemDto(of(userOrderItemsDetails))
                .totalPrices(new BigDecimal("11500"))
                .orderStatus(PENDING)
                .build();

        givenHelper(() -> serviceOrder.createOrder(anyLong()), expectedUserOrderDetails);

        mockMvc.perform(post("/check-out/order")
                .with(user(fakeUser))
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("This order placed successfully."));
    }
}
