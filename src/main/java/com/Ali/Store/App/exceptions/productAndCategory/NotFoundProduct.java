package com.Ali.Store.App.exceptions.productAndCategory;

import static java.lang.String.format;

public class NotFoundProduct extends RuntimeException {
    public NotFoundProduct(Object productId) {
        super(format("Not found this product [%s] !", productId));
    }
}
