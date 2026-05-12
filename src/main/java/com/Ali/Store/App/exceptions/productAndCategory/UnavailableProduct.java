package com.Ali.Store.App.exceptions.productAndCategory;

public class UnavailableProduct extends RuntimeException {
    public UnavailableProduct(Long productId) {
        super("This product [" + productId + "] is unavailable !");
    }
}
