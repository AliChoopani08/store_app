package com.Ali.Store.App.dto.checkout.response;

import com.Ali.Store.App.service.checkOut.order.OrderStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record UserOrderDetailsDto(@JsonProperty("User id") Long userId, @JsonProperty("Order id") Long orderId,
                                  @JsonProperty("Order items details")List<OrderItemDetailsDto> orderItemDto, @JsonProperty("Total prices") long totalPrices
        , @JsonProperty("Order status") OrderStatus orderStatus) {
}
