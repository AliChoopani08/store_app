package com.Ali.Store.App.exceptions.checkout;

public class InsufficientProductQuantity extends RuntimeException {
    public InsufficientProductQuantity(String message) {
        super(message);
    }
}
