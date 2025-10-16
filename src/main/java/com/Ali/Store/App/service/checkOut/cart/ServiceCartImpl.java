package com.Ali.Store.App.service.checkOut.cart;

import com.Ali.Store.App.dto.checkout.request.AddToCartRequest;
import com.Ali.Store.App.dto.checkout.response.CartItemDto;
import com.Ali.Store.App.dto.checkout.response.UserCartDetailsDto;
import com.Ali.Store.App.entities.checkout.Cart;
import com.Ali.Store.App.entities.checkout.CartItems;
import com.Ali.Store.App.entities.productAndCategory.Product;
import com.Ali.Store.App.entities.userAndProfileUser.Users;
import com.Ali.Store.App.exceptions.checkout.InsufficientProductQuantity;
import com.Ali.Store.App.exceptions.checkout.NotFoundCartItem;
import com.Ali.Store.App.exceptions.productAndCategory.NotFoundProduct;
import com.Ali.Store.App.exceptions.productAndCategory.UnavailableProduct;
import com.Ali.Store.App.exceptions.user.NotFoundUser;
import com.Ali.Store.App.repository.checkout.RepositoryCart;
import com.Ali.Store.App.repository.checkout.RepositoryCartItems;
import com.Ali.Store.App.repository.productAndCategory.RepositoryProduct;
import com.Ali.Store.App.repository.userAndProfileUser.RepositoryUser;
import com.Ali.Store.App.service.product.ItemStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BinaryOperator;

import static com.Ali.Store.App.service.product.ItemStatus.CREATED;
import static com.Ali.Store.App.service.product.ItemStatus.INCREASED;

/**
 * Service for managing user's shopping cart.
 *
 * <p>Purpose:<br>
 * This Service implements the logic for the adding
 * a product to a user's cart in a way that prevents
 * duplicate items and provides an optimized user
 * experience.
 * <p>
 *
 * <p>Scenario:<br>
 * When a user adds a product to the cart,
 * two cases may occur:<br>
 * 1. The product already exists in the
 * cart -> only the quantity is incremented.<br>
 * 2. The product does not exist in the
 * cart -> a new CartItem is created and added to cart.
 * </p>
 *
 * <p>Workflow:<br>
 * 1. Identify the logged-in user using @AuthenticationPrincipal.<br>
 * 2. Check if the product exists in the cart.<br>
 * 3. Handle two cases :<br>
 *              - Existing product: update the quantity.
 *              - New product: create a new CartItem and add
 *              it to the cart.<br>
 * 4. Check if the user has a cart in database.<br>
 * 5. Handle two cases :<br>
 *              - Has cart: The cart-items are added to existing cart.<br>
 *              - Doesn't have cart: A new cart is created for this user. <br>
 * 6. Save the changes to the database.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class ServiceCartImpl implements ServiceCartInterface{

    private final RepositoryUser repositoryUser;
    private final RepositoryProduct repositoryProduct;
    private final RepositoryCartItems repositoryCartItems;
    private final RepositoryCart repositoryCart;


    /**
     * @param userId      : The ID of current user
     * @param cartRequest : Request object for adding to cart includes<br>
     *                    - productId
     *                    - quantity.
     * @return Map with keys: <br>
     * - status : Item-status (CREATED or INCREASED)<br>
     * - data : Display user's order details
     */

    @Override
    @Transactional
    public Map<String, Object> addToCart(Long userId, AddToCartRequest cartRequest) {
        Map<String, Object> responseMap = new HashMap<>();
        Cart OperationsOutcome;

        AtomicReference<ItemStatus> status = new AtomicReference<>();
        final Users currentUser = getCurrentUserObject(userId);

        final Product foundProduct = repositoryProduct.findById(cartRequest.getProductId())
                .orElseThrow(() -> new NotFoundProduct("This product is not exist in database !"));

        if (!foundProduct.isAvailable()) {
            throw new UnavailableProduct("This product is unavailable !");
        }

        if (foundProduct.getQuantity() < cartRequest.getQuantity()) {
            throw new InsufficientProductQuantity("Only " + foundProduct.getQuantity() + " items is available. But " + cartRequest.getQuantity() + " requested !");
        }
        if ((foundProduct.getQuantity() - cartRequest.getQuantity()) < 0) {
            throw new InsufficientProductQuantity("There's insufficient quantity of this product in stock !");
        }


        final Optional<Cart> foundUserCartOptional = repositoryCart.findByUserId(currentUser.getProfile().getId());
        if (foundUserCartOptional.isPresent()) {

            OperationsOutcome = repositoryCartItems.findByProduct(foundProduct)
                    .map(item -> {
                        item.setQuantity(item.getQuantity() + cartRequest.getQuantity());

                        System.out.println("before operation .... " + foundProduct.getQuantity());
                        foundProduct.setQuantity(foundProduct.getQuantity() - cartRequest.getQuantity());
                        System.out.println("after operation .... " + foundProduct.getQuantity());

                        status.set(INCREASED);
                        return item.getCart();

                    })
                    .orElseGet(() -> {
                        final Cart UserCart = foundUserCartOptional.get();
                        final CartItems item = new CartItems();

                        item.setProduct(foundProduct);
                        item.setQuantity(cartRequest.getQuantity());
                        UserCart.getCartItems().add(item);
                        foundProduct.setQuantity(foundProduct.getQuantity() - cartRequest.getQuantity());

                        UserCart.addCartItems(UserCart.getCartItems());

                        status.set(CREATED);
                        return UserCart;
                    });
        } else {
            Cart cart = new Cart();
            currentUser.addCart(cart);

            final CartItems item = new CartItems();
            item.setProduct(foundProduct);
            item.setQuantity(cartRequest.getQuantity());
            foundProduct.setQuantity(foundProduct.getQuantity() - cartRequest.getQuantity());

            List<CartItems> cartItems = new LinkedList<>();
            cartItems.add(item);
            cart.addCartItems(cartItems);

            OperationsOutcome = cart;
        }

        if (foundProduct.getQuantity() == 0) {
            foundProduct.setAvailable(false);
        }

        repositoryProduct.save(foundProduct);
        repositoryCart.save(OperationsOutcome);

        final List<CartItemDto> cartItemsDetails = repositoryCartItems.findRequestedUserCartItemsDetails(currentUser.getId());

        final Integer totalPrice = getTotalPriceOfProduct(cartItemsDetails);

        responseMap.put("status", status.get());
        responseMap.put("Cart details", new UserCartDetailsDto(currentUser.getProfile().getId(), cartItemsDetails, totalPrice));

        return responseMap;

    }

    @Override
    public UserCartDetailsDto displayUserCartDetails(Long userId) {
        final Users currentUser = getCurrentUserObject(userId);
        final List<CartItemDto> userCartItemsDetails = repositoryCartItems.findRequestedUserCartItemsDetails(currentUser.getProfile().getId());

        return new UserCartDetailsDto(currentUser.getProfile().getId(), userCartItemsDetails, getTotalPriceOfProduct(userCartItemsDetails));
    }

    @Override
    @Transactional
    public void removeUserCartItemById(Long cartItemId, Integer quantity) {
        final CartItems foundCartItem = repositoryCartItems.findById(cartItemId)
                .orElseThrow(() -> new NotFoundCartItem("This cart item is not exist in database !"));

        foundCartItem.setQuantity(foundCartItem.getQuantity() - quantity);
        foundCartItem.getProduct().setQuantity(foundCartItem.getProduct().getQuantity() + quantity);
        repositoryProduct.save(foundCartItem.getProduct());

        if (foundCartItem.getQuantity() <= 0) {
            repositoryCartItems.delete(foundCartItem);
        }

        repositoryCartItems.save(foundCartItem);
    }

    private static Integer getTotalPriceOfProduct(List<CartItemDto> cartItemsDetails) {
        BinaryOperator<Integer> calculateTotalPrice = Integer::sum;
        return cartItemsDetails.stream()
                .map(CartItemDto::totalPriceOfProduct)
                .reduce(0, calculateTotalPrice);
    }

    private Users getCurrentUserObject(Long userId) {
        return repositoryUser.findById(userId)
                .orElseThrow(() -> new NotFoundUser("This User Is not exist in database !"));
    }
}
