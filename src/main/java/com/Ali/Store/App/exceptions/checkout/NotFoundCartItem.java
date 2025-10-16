package com.Ali.Store.App.exceptions.checkout;

public class NotFoundCartItem extends RuntimeException {
    public NotFoundCartItem(String message) {
        super(message);
    }
}
