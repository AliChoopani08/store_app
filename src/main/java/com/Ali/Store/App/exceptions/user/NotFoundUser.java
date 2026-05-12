package com.Ali.Store.App.exceptions.user;

public class NotFoundUser extends RuntimeException{
    public NotFoundUser(Object id) {
        super("This user [" + id + "] not found !");
    }
}
