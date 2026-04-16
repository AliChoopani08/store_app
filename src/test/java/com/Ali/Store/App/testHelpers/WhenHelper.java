package com.Ali.Store.App.testHelpers;

import static org.mockito.Mockito.when;

public class WhenHelper {

    public static <T> void whenHelper(T callMethod, T expectedResponse) {
        when(callMethod)
                .thenReturn(expectedResponse);
    }
}
