package com.Ali.Store.App.exceptions.user;

public class NotFoundUser extends RuntimeException{
    public NotFoundUser(String message) {
        super(message);
    }
}
