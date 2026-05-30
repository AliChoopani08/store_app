package com.Ali.Store.App.exceptions.productAndCategory;

import static java.lang.String.format;

public class NotFoundCategory extends RuntimeException {
    public NotFoundCategory(Object categoryId) {
        super(format("Not found this category [%s] !", categoryId));
    }
}
