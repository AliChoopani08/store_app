package com.Ali.Store.App.exceptions.user;

import static java.lang.String.format;

public class DuplicateUsername extends RuntimeException {
  public DuplicateUsername(String username) {
    super(format("This User [%s]is already registered !. Please login with this username", username));
  }
}
