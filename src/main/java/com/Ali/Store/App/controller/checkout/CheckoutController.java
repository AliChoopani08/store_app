package com.Ali.Store.App.controller.checkout;

import com.Ali.Store.App.dto.checkout.request.AddToCartRequest;
import com.Ali.Store.App.dto.checkout.response.UserCartDetailsDto;
import com.Ali.Store.App.dto.checkout.response.UserOrderDetailsDto;
import com.Ali.Store.App.dto.product.response.ApiResponse;
import com.Ali.Store.App.security.userDetails.UserDetailsImpl;
import com.Ali.Store.App.service.checkOut.cart.ServiceCartInterface;
import com.Ali.Store.App.service.checkOut.order.ServiceOrderInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import static com.Ali.Store.App.service.product.ItemStatus.INCREASED;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

@Tag(name = "Check out API", description = "Operations related to adding products to cart and placing an order")
@RestController
@RequestMapping("/check-out")
@RequiredArgsConstructor
public class CheckoutController {

    private final ServiceOrderInterface serviceOrder;
    private final ServiceCartInterface serviceCart;

    @PostMapping("/cart")
    @Operation(
            summary = "Add Product To Cart",
            description = """
                    Behavior: \n
                    -If this entered product exist in the user's cart -> Its quantity is increated.
                    - If this entered product doesn't exist in the user's cart -> A new cart-item is created and added to user's cart.
                    """
    )
    public ResponseEntity<ApiResponse<Object>> addingToUserCart(@AuthenticationPrincipal UserDetailsImpl currentUser,
                                                                            @RequestBody @Valid AddToCartRequest addToCartRequest) {

        final Map<String, Object> userCartDetailsResponse = serviceCart.addToCart(currentUser.getId(), addToCartRequest);

        int statusOfOperation = userCartDetailsResponse.get("status") == INCREASED ? 200 : 201;
        String messageOfOperationResult = userCartDetailsResponse.get("status") == INCREASED ? "The product quantity increased successfully." :
                                            "A new cart item created successfully.";

        return status(HttpStatus.valueOf(statusOfOperation))
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
    public ResponseEntity<Void> deleteProductFromUserCart(@PathVariable("cart-itemId") Long cartItemId, @PathVariable Integer quantity) {
        serviceCart.removeUserCartItemById(cartItemId, quantity);

        return status(NO_CONTENT)
                .build();
    }


    @PostMapping("/order")
    @Operation(
            summary = "Place an order"
    )
    public ResponseEntity<ApiResponse<UserOrderDetailsDto>> placingAnOrder(@AuthenticationPrincipal UserDetailsImpl currentUser) {
        final UserOrderDetailsDto userOrderDetailsResponse = serviceOrder.createOrder(currentUser.getId());

        return status(CREATED)
                .body(new ApiResponse<>(201, "This order placed successfully.", userOrderDetailsResponse));
    }

}
