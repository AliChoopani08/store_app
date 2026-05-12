package com.Ali.Store.App.exceptions.checkout;

public class NotFoundCart extends RuntimeException {
    public NotFoundCart(Long userId) {
        super("Not found user [" + userId + "]'s cart !");
    }
}
