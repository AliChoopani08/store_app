package com.Ali.Store.App.exceptions.productAndCategory;

public class UnavailableProduct extends RuntimeException {
    public UnavailableProduct(String message) {
        super(message);
    }
}
