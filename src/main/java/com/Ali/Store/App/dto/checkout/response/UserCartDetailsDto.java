package com.Ali.Store.App.dto.checkout.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record UserCartDetailsDto(@JsonProperty("User id") Long userid,
                                 @JsonProperty("User cart details") List<CartItemDto> cartItemsDto, @JsonProperty("Total all prices") long totalPrices) {
}
