package com.Ali.Store.App.exceptions.checkout;

import static java.lang.String.format;

public class NotFoundCartItem extends RuntimeException {
    public NotFoundCartItem(Long itemId) {
        super(format("This cart item [%d] is not exist in database !", itemId));
    }
}
