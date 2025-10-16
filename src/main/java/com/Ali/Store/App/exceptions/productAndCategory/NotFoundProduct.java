package com.Ali.Store.App.exceptions.productAndCategory;

public class NotFoundProduct extends RuntimeException {
    public NotFoundProduct(String message) {
        super(message);
    }
}
