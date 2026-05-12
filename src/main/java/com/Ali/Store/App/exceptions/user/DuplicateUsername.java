package com.Ali.Store.App.exceptions.user;

public class DuplicateUsername extends RuntimeException {
  public DuplicateUsername(String username) {
    super("This User [" + username + "]is already registered !. Please login with this username");
  }
}
