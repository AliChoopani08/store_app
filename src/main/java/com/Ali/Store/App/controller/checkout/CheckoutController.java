package com.Ali.Store.App.controller.checkout;

import com.Ali.Store.App.dto.checkout.request.AddToCartRequest;
import com.Ali.Store.App.dto.checkout.response.UserCartDetailsDto;
import com.Ali.Store.App.dto.checkout.response.UserOrderDetailsDto;
import com.Ali.Store.App.dto.product.response.ApiResponse;
import com.Ali.Store.App.security.userDetails.UserDetailsImpl;
import com.Ali.Store.App.service.checkOut.cart.CartService;
import com.Ali.Store.App.service.checkOut.order.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import static com.Ali.Store.App.service.product.ItemStatus.INCREASED;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

@Tag(name = "Check out API", description = "Operations related to adding products to cart and placing an order")
@RestController
@RequestMapping("/check-out")
@RequiredArgsConstructor
@Slf4j
public class CheckoutController {

    private final OrderService serviceOrder;
    private final CartService serviceCart;

    @PostMapping("/cart")
    @Operation(
            summary = "Add Product To Cart",
            description = """
                    Behavior: \n
                    -If this entered product exist in the user's cart -> Its quantity is increated.
                    - If this entered product doesn't exist in the user's cart -> A new cart-item is created and added to user's cart.
                    """
    )
    public ResponseEntity<ApiResponse<Object>> addProductToUserCart(@AuthenticationPrincipal UserDetailsImpl currentUser,
                                                                    @RequestBody @Valid AddToCartRequest addToCartRequest) {

        log.info("API request: add product to user [{}] cart...", currentUser.getId());
        final Map<String, Object> userCartDetailsResponse = serviceCart.addToCart(currentUser.getId(), addToCartRequest);

        int statusOfOperation = userCartDetailsResponse.get("status") == INCREASED ? 200 : 201;
        String messageOfOperationResult = userCartDetailsResponse.get("status") == INCREASED ? "The product quantity increased successfully." :
                                            "A new cart item created successfully.";

        return status(valueOf(statusOfOperation))
                .body(new ApiResponse<>(statusOfOperation, messageOfOperationResult, userCartDetailsResponse.get("Cart details")));
    }

    @GetMapping("/cart")
    @Operation(
            summary = "Display The User's Cart Details"
    )
    public ResponseEntity<ApiResponse<UserCartDetailsDto>> displayUserCartDetails(@AuthenticationPrincipal UserDetailsImpl currentUser) {
        final UserCartDetailsDto userCartDetails = serviceCart.displayUserCartDetails(currentUser.getId());

        return ok(new ApiResponse<>(200, "The user cart details returned successfully.", userCartDetails));
    }


    @DeleteMapping("/cart/{cart-itemId}/{quantity}")
    @Operation(
            summary = "Delete Cart-Item From User's Cart",
            description = "This operation removes the found cart-Item by its ID in the current user's cart."
    )
    public ResponseEntity<Void> reduceCartItemQuantity(@AuthenticationPrincipal UserDetailsImpl currentUser, @PathVariable("cart-itemId") Long cartItemId,
                                                       @PathVariable Integer quantity) {
        log.info("API request: reduce cart item from user [{}] cart, cart item[{}]...", currentUser.getId(), cartItemId);

        serviceCart.reduceCartItemQuantity(currentUser.getId(), quantity, cartItemId);

        return status(NO_CONTENT)
                .build();
    }


    @PostMapping("/order")
    @Operation(
            summary = "Place an order"
    )
    public ResponseEntity<ApiResponse<UserOrderDetailsDto>> placingAnOrder(@AuthenticationPrincipal UserDetailsImpl currentUser) {
        log.info("API request: place an order for user [{}]...", currentUser.getId());

        final UserOrderDetailsDto userOrderDetailsResponse = serviceOrder.createOrder(currentUser.getId());

        return status(CREATED)
                .body(new ApiResponse<>(201, "This order placed successfully.", userOrderDetailsResponse));
    }

}
