package com.Ali.Store.App.dto.checkout.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
public record UserCartDetailsDto(@JsonProperty("User id") Long userid,
                                 @JsonProperty("User cart details") List<CartItemDto> cartItemsDto) {
}
