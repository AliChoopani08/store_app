package com.Ali.Store.App.dto.product.response;

import java.math.BigDecimal;

public record ProductDtoForCartItems(Long id, String name, BigDecimal price, Boolean status) {
}
