package com.Ali.Store.App.exceptions.checkout;

import static java.lang.String.format;

public class NotFoundCart extends RuntimeException {
    public NotFoundCart(Long userId) {
        super(format("Not found this the cart for user [%d}", userId));
    }
}
