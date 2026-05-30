package com.Ali.Store.App.exceptions.checkout;

import static java.lang.String.format;

public class InsufficientProductQuantity extends RuntimeException {
    public InsufficientProductQuantity(int availableQuantity, int requestedQuantity) {
        super(format("Only [%d] items is available. But [%d] requested !", availableQuantity, requestedQuantity));
    }
}
