package com.Ali.Store.App.service.checkOut.cart;

import com.Ali.Store.App.dto.checkout.request.AddToCartRequest;
import com.Ali.Store.App.dto.checkout.response.UserCartDetailsDto;

import java.util.Map;

public interface CartService {
    Map<String, Object> addToCart(Long userId, AddToCartRequest cartRequest);
    UserCartDetailsDto displayUserCartDetails(Long userId);
    void reduceCartItemQuantity(Long cartItemId, Integer quantity);
}
