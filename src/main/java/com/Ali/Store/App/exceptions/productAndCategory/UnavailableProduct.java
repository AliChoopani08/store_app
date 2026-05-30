package com.Ali.Store.App.exceptions.productAndCategory;

import static java.lang.String.format;

public class UnavailableProduct extends RuntimeException {
    public UnavailableProduct(Long productId) {
        super(format("This product [%d] is unavailable !", productId));
    }
}
