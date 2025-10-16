package com.Ali.Store.App.exceptions.productAndCategory;

public class NotFoundCategory extends RuntimeException {
    public NotFoundCategory(String message) {
        super(message);
    }
}
