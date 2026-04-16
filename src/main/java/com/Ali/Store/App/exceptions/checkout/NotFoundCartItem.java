package com.Ali.Store.App.exceptions.checkout;

public class NotFoundCartItem extends RuntimeException {
    public NotFoundCartItem(Long itemId) {
        super("This cart item [" + itemId + "] is not exist in database !");
    }
}
