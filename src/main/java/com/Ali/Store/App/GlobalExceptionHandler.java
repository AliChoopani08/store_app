package com.Ali.Store.App;

import com.Ali.Store.App.exceptions.checkout.InsufficientProductQuantity;
import com.Ali.Store.App.exceptions.checkout.NotFoundCart;
import com.Ali.Store.App.exceptions.checkout.NotFoundCartItem;
import com.Ali.Store.App.exceptions.productAndCategory.DuplicateCategory;
import com.Ali.Store.App.exceptions.productAndCategory.NotFoundCategory;
import com.Ali.Store.App.exceptions.productAndCategory.NotFoundProduct;
import com.Ali.Store.App.exceptions.productAndCategory.UnavailableProduct;
import com.Ali.Store.App.exceptions.security.*;
import com.Ali.Store.App.exceptions.user.DuplicateRefreshToken;
import com.Ali.Store.App.exceptions.user.DuplicateUsername;
import com.Ali.Store.App.exceptions.user.NotFoundDevice;
import com.Ali.Store.App.exceptions.user.NotFoundUser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
@Slf4j
public class GlobalExceptionHandler {


    // Invalid Inputs
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handlerValidationFieldsError(MethodArgumentNotValidException ex) {
        Map<String, String> errorsBody = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errorsBody.put(error.getField(), error.getDefaultMessage()));

        log.warn(errorsBody.toString());
        return status(BAD_REQUEST)
                .body(errorsBody);
    }

    // Not Found User
    @ExceptionHandler(NotFoundUser.class)
    public ResponseEntity<ResponseError> notFoundUserHandler(HttpServletRequest request, NotFoundUser ex) {
        log.warn(ex.getMessage());

        return getResponseError(NOT_FOUND, "Not Found", ex.getMessage(), request);
    }

    // Invalid URL
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ResponseError> invalidURLHandler(HttpServletRequest request) {
        final String errorMessage = "Not Found any action with this URL !";

        log.warn(errorMessage);

        return getResponseError(NOT_FOUND, "Invalid URL", errorMessage, request);
    }

    // Not Being Match URL And Method
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ResponseError> mistakeMethodAndURLHandler(HttpServletRequest request) {
        log.warn("URL: {} HTTPS method: {}", request.getRequestURI(), request.getMethod());

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
        final String errorMessage = "Entrance parameter is invalid ! Please try again...";

        log.warn(errorMessage);

        return getResponseError(BAD_REQUEST, "Invalid Parameter", errorMessage, request);
    }

    //invalid password
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> beingInvalidPasswordHandler(HttpServletRequest request, BadCredentialsException ex) {
        log.warn(ex.getMessage());

        return getResponseError(UNAUTHORIZED, "Unauthorized", ex.getMessage(), request);
    }

    // Duplicate category
    @ExceptionHandler(DuplicateCategory.class)
    public ResponseEntity<ResponseError> duplicateCategory (HttpServletRequest request, DuplicateCategory ex) {
        log.warn(ex.getMessage());

        return getResponseError(CONFLICT, "Duplicate Category", ex.getMessage(), request);
    }

    // Not Found Refresh Token
    @ExceptionHandler(NotFoundRefreshToken.class)
    public ResponseEntity<ResponseError> notFoundRefreshToken(HttpServletRequest request, NotFoundRefreshToken ex) {
        log.warn(ex.getMessage());

        return getResponseError(NOT_FOUND, "Not Found", ex.getMessage(), request);
    }


    // Not Found Category
    @ExceptionHandler(NotFoundCategory.class)
    public ResponseEntity<ResponseError> notFoundThisCategory(HttpServletRequest request, NotFoundCategory ex) {
        log.warn(ex.getMessage());

        return getResponseError(NOT_FOUND, "Not Found", ex.getMessage(), request);
    }

    // Unavailable Product
    @ExceptionHandler(UnavailableProduct.class)
    public ResponseEntity<ResponseError> unavailableProductHandler(HttpServletRequest request, UnavailableProduct ex) {
        log.warn(ex.getMessage());

        return getResponseError(CONFLICT, "Unavailable", ex.getMessage(), request);
    }

    //Not Found Product
    @ExceptionHandler(NotFoundProduct.class)
    public ResponseEntity<ResponseError> notFoundProduct(HttpServletRequest request, NotFoundProduct ex) {
        log.warn(ex.getMessage());

        return getResponseError(NOT_FOUND, "Not Found", ex.getMessage(), request);
    }

    // Not Enough The Quantity Of Product For This Order
    @ExceptionHandler(InsufficientProductQuantity.class)
    public ResponseEntity<ResponseError> notEnoughProductQuantityHandler(HttpServletRequest request, InsufficientProductQuantity ex) {
        log.warn(ex.getMessage());

        return getResponseError(CONFLICT, "Not Enough", ex.getMessage(), request);
    }

    // Handler Expired Jwt Password Token
    @ExceptionHandler(JwtPasswordExpiredException.class)
    public ResponseEntity<ResponseError> jwtPasswordExpiredHandle(HttpServletRequest request, JwtPasswordExpiredException ex) {
        log.warn(ex.getMessage());

        return getResponseError(BAD_REQUEST, "Expired Token", ex.getMessage(), request);
    }

    // Invalid password verify token
    @ExceptionHandler(PasswordVerifyTokenExceptions.class)
    public ResponseEntity<ResponseError> passwordVerifyTokenHandle(HttpServletRequest request, PasswordVerifyTokenExceptions ex) {
        log.warn(ex.getMessage());

        return getResponseError(BAD_REQUEST, "Invalid Token" , ex.getMessage(), request);
    }

    // Duplicate refresh token
    @ExceptionHandler(DuplicateRefreshToken.class)
    public ResponseEntity<ResponseError> duplicateRefreshTokenHandler(HttpServletRequest request, DuplicateRefreshToken ex) {
        log.warn(ex.getMessage());

        return  getResponseError(CONFLICT, "Duplicate Refresh Token", ex.getMessage(), request);
    }

    // Duplicate username
    @ExceptionHandler(DuplicateUsername.class)
    public ResponseEntity<ResponseError> duplicateUsernameHandler(HttpServletRequest request, DuplicateUsername ex) {
        log.warn(ex.getMessage());

        return getResponseError(CONFLICT, "Duplicate Username", ex.getMessage(), request);
    }

    // Not Found Cart Item
    @ExceptionHandler(NotFoundCartItem.class)
    public ResponseEntity<ResponseError> notFoundCartItemHandler(HttpServletRequest request, NotFoundCartItem ex) {
        log.warn(ex.getMessage());

        return getResponseError(NOT_FOUND, "Not Found", ex.getMessage(), request);
    }

    // Not found user's cart
    @ExceptionHandler(NotFoundCart.class)
    public ResponseEntity<ResponseError> notFoundCartHandler(HttpServletRequest request, NotFoundCart ex) {
        log.warn(ex.getMessage());

        return getResponseError(NOT_FOUND, "Not Found", ex.getMessage(), request);
    }

    // Not Found Device
    @ExceptionHandler(NotFoundDevice.class)
    public ResponseEntity<ResponseError> notFoundDeviceHandler(HttpServletRequest request, NotFoundDevice ex) {
        log.warn(ex.getMessage());

        return getResponseError(NOT_FOUND, "Not Found", ex.getMessage(), request);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ResponseError> notFoundUsernameHandler(HttpServletRequest request, UsernameNotFoundException ex) {
        log.warn(ex.getMessage());

        return getResponseError(NOT_FOUND, "Not Found", ex.getMessage(), request);
    }

    @ExceptionHandler(DeviceNotAllowedException.class)
    public ResponseEntity<ResponseError> deviceNotAllowedHandler(HttpServletRequest request, DeviceNotAllowedException ex) {
        log.warn(ex.getMessage());

        return getResponseError(LOCKED, "Not Allowed", ex.getMessage(), request);
    }

    @ExceptionHandler(ExpiredRefreshToken.class)
    public ResponseEntity<ResponseError> expiredRefreshTokenHandler(HttpServletRequest request, ExpiredRefreshToken ex) {
        log.warn(ex.getMessage());

        return getResponseError(UNAUTHORIZED, "Unauthorized", ex.getMessage(), request);
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
