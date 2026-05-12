package com.Ali.Store.App.dto.checkout.response;

import com.Ali.Store.App.service.checkOut.order.OrderStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record UserOrderDetailsDto(@JsonProperty("User id") Long userId, @JsonProperty("Order id") Long orderId,
                                  @JsonProperty("Order items details") List<OrderItemDetailsDto> orderItemDto,
                                  @JsonProperty("Total prices") BigDecimal totalPrices
        , @JsonProperty("Order status") OrderStatus orderStatus) {
}
