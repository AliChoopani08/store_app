package com.Ali.Store.App.checkOut.controller;

import com.Ali.Store.App.controller.checkout.CheckoutController;
import com.Ali.Store.App.dto.checkout.request.AddToCartRequest;
import com.Ali.Store.App.dto.checkout.response.CartItemDto;
import com.Ali.Store.App.dto.checkout.response.OrderItemDetailsDto;
import com.Ali.Store.App.dto.checkout.response.UserCartDetailsDto;
import com.Ali.Store.App.dto.checkout.response.UserOrderDetailsDto;
import com.Ali.Store.App.entities.userAndProfileUser.ProfileUser;
import com.Ali.Store.App.security.userDetails.UserDetailsImpl;
import com.Ali.Store.App.service.checkOut.cart.ServiceCartInterface;
import com.Ali.Store.App.service.checkOut.order.ServiceOrderInterface;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.Ali.Store.App.service.checkOut.order.OrderStatus.PENDING;
import static com.Ali.Store.App.service.product.ItemStatus.CREATED;
import static java.util.List.of;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CheckoutController.class)
@Import(ConfigTestCheckOutController.class)
public class CheckoutControllerShould {

    @Autowired
    private ServiceOrderInterface serviceOrder;
    @Autowired
    private ServiceCartInterface serviceCart;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private UserDetailsImpl fakeUser;

    @BeforeEach
    void setUp() {
        final ProfileUser profileUser = new ProfileUser("Mohammad", null, null, LocalDate.of(1992, 9, 12));
        fakeUser = new UserDetailsImpl("09112223344", null,null,10L, profileUser);
    }

    @Test
    void add_to_cart() throws Exception {
        final UserCartDetailsDto userCartDetails = new UserCartDetailsDto(10L, of(new CartItemDto(1L, 3L, "Mint Yogurt", "Food", 11500,5)), 11500);
        final AddToCartRequest addToCartRequest = new AddToCartRequest(3L, 5);

        Map<String, Object> expectedResponseMap = new HashMap<>();
        expectedResponseMap.put("status", CREATED); // -> Assume this product does not exist in the cart
        expectedResponseMap.put("Cart details", userCartDetails);

        given(serviceCart.addToCart(any(Long.class), any(AddToCartRequest.class)))
                .willReturn(expectedResponseMap);

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
    void deleter_product_from_cart() throws Exception {
        willDoNothing().given(serviceCart).removeUserCartItemById(any(Long.class), any(Integer.class));

        mockMvc.perform(delete("/check-out/cart/3")
                .with(user(fakeUser))
                .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void place_an_order() throws Exception {
        final OrderItemDetailsDto userOrderItemsDetails = new OrderItemDetailsDto(1L, 3L, "Mint Yogurt","mint-yogurt", 11500, 5);
        UserOrderDetailsDto expectedUserOrderDetails = new UserOrderDetailsDto(10L, 5L, List.of(userOrderItemsDetails), 11500, PENDING);

        given(serviceOrder.createOrder(any(Long.class)))
                .willReturn(expectedUserOrderDetails);

        mockMvc.perform(post("/check-out/order")
                .with(user(fakeUser))
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("This order placed successfully."));
    }
}
