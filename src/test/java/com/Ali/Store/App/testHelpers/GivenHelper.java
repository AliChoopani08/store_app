package com.Ali.Store.App.testHelpers;

import java.util.function.Supplier;

import static org.mockito.BDDMockito.given;

public class GivenHelper {

    public static <T> void givenHelper(Supplier<T> callMethod, T response) {
        given(callMethod.get())
                .willReturn(response);
    }
}
