package com.Ali.Store.App.service.checkOut.order;

import com.Ali.Store.App.dto.checkout.response.UserOrderDetailsDto;

public interface ServiceOrderInterface {
    UserOrderDetailsDto createOrder(Long userId);
}
