package com.Ali.Store.App.exceptions.user;

import java.util.UUID;

import static java.lang.String.format;

public class NotFoundDevice extends RuntimeException {
    public NotFoundDevice(UUID deviceUuid) {
        super(format("This device [%s] does not exist !",deviceUuid));
    }
}
