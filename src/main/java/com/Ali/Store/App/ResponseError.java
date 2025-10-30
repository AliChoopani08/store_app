package com.Ali.Store.App;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ResponseError(@JsonFormat(shape = JsonFormat.Shape.STRING
        , pattern = "yyyy-MM-dd-HH:mm:ss") LocalDateTime dateTime
        , int status, String error, String message, String path) {}
