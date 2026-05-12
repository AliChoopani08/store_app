package com.Ali.Store.App.exceptions.user;

import java.util.UUID;

public class NotFoundDevice extends RuntimeException {
    public NotFoundDevice(UUID deviceUuid) {
        super("This device [" + deviceUuid + "] does not exist !");
    }
}
