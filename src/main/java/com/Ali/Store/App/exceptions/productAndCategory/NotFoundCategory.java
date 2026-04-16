package com.Ali.Store.App.exceptions.productAndCategory;

public class NotFoundCategory extends RuntimeException {
    public NotFoundCategory(Object categoryId) {
        super("Not found this category [" + categoryId + "] !");
    }
}
