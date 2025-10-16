package com.Ali.Store.App;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record ResponseError(@JsonFormat(pattern = "yyyy-MM-dd-HH:mm:ss") LocalDateTime dateTime, int status, String error, String message, String path) {}
