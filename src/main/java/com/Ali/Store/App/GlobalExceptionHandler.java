package com.Ali.Store.App;

import com.Ali.Store.App.exceptions.checkout.InsufficientProductQuantity;
import com.Ali.Store.App.exceptions.productAndCategory.NotFoundCategory;
import com.Ali.Store.App.exceptions.productAndCategory.NotFoundProduct;
import com.Ali.Store.App.exceptions.productAndCategory.UnavailableProduct;
import com.Ali.Store.App.exceptions.security.JwtPasswordExpiredException;
import com.Ali.Store.App.exceptions.security.NotFoundRefreshToken;
import com.Ali.Store.App.exceptions.DuplicateValueException;
import com.Ali.Store.App.exceptions.security.PasswordVerifyTokenExceptions;
import com.Ali.Store.App.exceptions.user.DuplicateRefreshToken;
import com.Ali.Store.App.exceptions.user.DuplicateUsername;
import com.Ali.Store.App.exceptions.user.NotFoundUser;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.ResponseEntity.status;

@RestControllerAdvice
public class GlobalExceptionHandler {


    // Invalid Inputs
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handlerValidationFieldsError(MethodArgumentNotValidException ex) {
        Map<String, String> errorsBody = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errorsBody.put(error.getField(), error.getDefaultMessage()));

        return status(BAD_REQUEST)
                .body(errorsBody);
    }

    // Not Found User
    @ExceptionHandler(NotFoundUser.class)
    public ResponseEntity<ResponseError> handlerNotFoundUserError(HttpServletRequest request, NotFoundUser ex) {

        return getResponseError(NOT_FOUND, "Not Found", ex.getMessage(), request);
    }

    // Invalid URL
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ResponseError> invalidURL(HttpServletRequest request) {

        return getResponseError(NOT_FOUND, "Invalid URL", "Not Found any action with this URL !", request);
    }

    // Not Being Match URL And Method
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ResponseError> mistakeMethodAndURL(HttpServletRequest request) {

        ResponseError responseError = new ResponseError(now()
                , METHOD_NOT_ALLOWED.value()
                , "Not Being Match URL And Method"
                , "This URL isn't match with this method !"
                , "URL: " + request.getRequestURI() + " HTTPS method: " + request.getMethod());

        return status(NOT_FOUND)
                .body(responseError);
    }

    // Invalid Parameter
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ResponseError> invalidParameterHandler(HttpServletRequest request) {

        return getResponseError(BAD_REQUEST, "Invalid Parameter", "Entrance parameter is invalid ! Please try again...", request);
    }

    //invalid password
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentialsException(HttpServletRequest request) {

        return getResponseError(UNAUTHORIZED, "Unauthorized", "Password is invalid", request);
    }

    // Duplicate username
    @ExceptionHandler(DuplicateValueException.class)
    public ResponseEntity<ResponseError> duplicateUsernameException(HttpServletRequest request, DuplicateValueException ex) {

        return getResponseError(CONFLICT, "Duplicate Value", ex.getMessage(), request);
    }

    // Expired token
    @ExceptionHandler(ExpiredJwtException.class) // make correct it later
    public ResponseEntity<ResponseError> expiredTokenException(HttpServletRequest request) {

        return getResponseError(UNAUTHORIZED, "Unauthorized", "Your token is expired !", request);
    }

    // Not Found Refresh Token
    @ExceptionHandler(NotFoundRefreshToken.class)
    public ResponseEntity<ResponseError> notFoundRefreshToken(HttpServletRequest request, NotFoundRefreshToken ex) {
        return getResponseError(NOT_FOUND, "Not Found", ex.getMessage(), request);
    }


    // Not Found Category
    @ExceptionHandler(NotFoundCategory.class)
    public ResponseEntity<ResponseError> notFoundThisCategory(HttpServletRequest request, NotFoundCategory ex) {
        return getResponseError(NOT_FOUND, "Not Found", ex.getMessage(), request);
    }

    // Unavailable Product
    @ExceptionHandler(UnavailableProduct.class)
    public ResponseEntity<ResponseError> unavailableProduct(HttpServletRequest request, UnavailableProduct ex) {
        return getResponseError(CONFLICT, "Unavailable", ex.getMessage(), request);
    }

    //Not Found Product
    @ExceptionHandler(NotFoundProduct.class)
    public ResponseEntity<ResponseError> notFoundProduct(HttpServletRequest request, NotFoundProduct ex) {
        return getResponseError(NOT_FOUND, "Not Found", ex.getMessage(), request);
    }

    // Not Enough The Quantity Of Product For This Order
    @ExceptionHandler(InsufficientProductQuantity.class)
    public ResponseEntity<ResponseError> notEnoughProductQuantity(HttpServletRequest request, InsufficientProductQuantity ex) {
        return getResponseError(CONFLICT, "Not Enough", ex.getMessage(), request);
    }

    // Handler Expired Jwt Password Token
    @ExceptionHandler(JwtPasswordExpiredException.class)
    public ResponseEntity<ResponseError> jwtPasswordExpiredHandle(HttpServletRequest request, JwtPasswordExpiredException ex) {
        return getResponseError(BAD_REQUEST, "Expired Token", ex.getMessage(), request);
    }

    @ExceptionHandler(PasswordVerifyTokenExceptions.class)
    public ResponseEntity<ResponseError> passwordVerifyTokenHandle(HttpServletRequest request, PasswordVerifyTokenExceptions ex) {
        return getResponseError(BAD_REQUEST, "Invalid Token" , ex.getMessage(), request);
    }

    @ExceptionHandler(DuplicateRefreshToken.class)
    public ResponseEntity<ResponseError> duplicateRefreshTokenHandler(HttpServletRequest request, DuplicateRefreshToken ex) {
        return  getResponseError(CONFLICT, "Duplicate Refresh Token", ex.getMessage(), request);
    }

    @ExceptionHandler(DuplicateUsername.class)
    public ResponseEntity<ResponseError> duplicateUsernameHandler(HttpServletRequest request, DuplicateUsername ex) {
        return getResponseError(CONFLICT, "Duplicate Username", ex.getMessage(), request);
    }



    private static ResponseEntity<ResponseError> getResponseError(HttpStatus status, String error, String message, HttpServletRequest request) {
        final ResponseError responseError = new ResponseError(now()
                , status.value()
                , error
                , message
                , request.getRequestURI());

        return status(status)
                .body(responseError);
    }

}
