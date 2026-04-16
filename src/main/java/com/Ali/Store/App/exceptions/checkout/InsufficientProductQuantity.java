package com.Ali.Store.App.exceptions.checkout;

public class InsufficientProductQuantity extends RuntimeException {
    public InsufficientProductQuantity(int availableQuantity, int requestedQuantity) {
        super("Only " + availableQuantity + " items is available. But " + requestedQuantity + " requested !");
    }
}
