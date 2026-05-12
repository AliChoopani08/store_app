package com.Ali.Store.App.exceptions.checkout;

public class NotFoundOrder extends RuntimeException {
    public NotFoundOrder(Long userId) {
        super("Not found user [" + userId + "]'s order !");
    }
}
