package com.Ali.Store.App.exceptions.productAndCategory;

import static java.lang.String.format;

public class DuplicateCategory extends RuntimeException {
    public DuplicateCategory(String name) {
        super(format("This category [%s] is already exist !", name));
    }
}
