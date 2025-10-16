package com.Ali.Store.App.exceptions.checkout;

public class NotFoundCart extends RuntimeException {
    public NotFoundCart(String message) {
        super(message);
    }
}
