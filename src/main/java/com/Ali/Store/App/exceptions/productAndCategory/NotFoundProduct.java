package com.Ali.Store.App.exceptions.productAndCategory;

public class NotFoundProduct extends RuntimeException {
    public NotFoundProduct(Object productId) {
        super("Not found this product [" + productId + "] !");
    }
}
