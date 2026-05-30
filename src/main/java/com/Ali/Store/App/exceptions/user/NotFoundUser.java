package com.Ali.Store.App.exceptions.user;

import static java.lang.String.format;

public class NotFoundUser extends RuntimeException{
    public NotFoundUser(Object id) {
        super(format("Not found This user [%s] !", id));
    }
}
