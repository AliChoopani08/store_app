package com.Ali.Store.App.service.checkOut.cart;

import com.Ali.Store.App.dto.checkout.request.AddToCartRequest;
import com.Ali.Store.App.dto.checkout.response.CartItemDto;
import com.Ali.Store.App.dto.checkout.response.UserCartDetailsDto;
import com.Ali.Store.App.entities.checkout.Cart;
import com.Ali.Store.App.entities.checkout.CartItem;
import com.Ali.Store.App.entities.productAndCategory.Product;
import com.Ali.Store.App.entities.userAndProfileUser.Users;
import com.Ali.Store.App.exceptions.checkout.InsufficientProductQuantity;
import com.Ali.Store.App.exceptions.checkout.NotFoundCartItem;
import com.Ali.Store.App.exceptions.productAndCategory.NotFoundProduct;
import com.Ali.Store.App.exceptions.productAndCategory.UnavailableProduct;
import com.Ali.Store.App.exceptions.user.NotFoundUser;
import com.Ali.Store.App.repository.checkout.CartRepository;
import com.Ali.Store.App.repository.checkout.CartItemsRepository;
import com.Ali.Store.App.repository.productAndCategory.ProductRepository;
import com.Ali.Store.App.repository.userAndProfileUser.UserRepository;
import com.Ali.Store.App.service.product.ItemStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

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
 * - Existing product: update the quantity.
 * - New product: create a new CartItem and add
 * it to the cart.<br>
 * 4. Check if the user has a cart in database.<br>
 * 5. Handle two cases :<br>
 * - Has cart: The cart-items are added to existing cart.<br>
 * - Doesn't have cart: A new cart is created for this user. <br>
 * 6. Save the changes to the database.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {

    private final UserRepository repositoryUser;
    private final ProductRepository repositoryProduct;
    private final CartItemsRepository repositoryCartItems;
    private final CartRepository repositoryCart;


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
        log.info("Starting to add product to user's cart, user id [{}]", userId);
        Map<String, Object> responseMap = new HashMap<>();
        AtomicReference<ItemStatus> status = new AtomicReference<>();
        final Users currentUser = getCurrentUserObject(userId);
        final Product foundProduct = getProduct(cartRequest.getProductId());

        checkProductStatus(cartRequest, foundProduct);

        final Optional<Cart> foundUserCartOptional = repositoryCart.findByUserId(currentUser.getId());

        final Cart OperationsOutcome = foundUserCartOptional.map(cart -> repositoryCartItems.findByCartAndProduct(cart.getId(), foundProduct.getId()) // if cart user exists
                        .map(item -> increaseItemQuantityIfExists(cartRequest.getQuantity(), item, foundProduct, status))
                        .orElseGet(() -> createNewItemIfDoesNotExist(cartRequest.getQuantity(), cart, foundProduct, status)))

                .orElseGet(() -> createNewCartIfDoesNotExist(cartRequest.getQuantity(), currentUser, foundProduct)); // cart user doesn't exist

        if (foundProduct.getQuantity() <= 0) {
            foundProduct.setAvailable(false);
        }

        repositoryProduct.save(foundProduct);
        repositoryCart.save(OperationsOutcome);

        final List<CartItemDto> cartItemsDetails = repositoryCartItems.findUserCartItemsDetails(currentUser.getId());

        responseMap.put("status", status.get());
        responseMap.put("Cart details", new UserCartDetailsDto(currentUser.getId(), cartItemsDetails));

        return responseMap;
    }

    @Override
    public UserCartDetailsDto displayUserCartDetails(Long userId) {
        final Users currentUser = getCurrentUserObject(userId);
        final List<CartItemDto> userCartItemsDetails = repositoryCartItems.findUserCartItemsDetails(currentUser.getProfile().getId());

        return new UserCartDetailsDto(currentUser.getProfile().getId(), userCartItemsDetails);
    }

    @Override
    @Transactional
    public void reduceCartItemQuantity(Long cartItemId, Integer quantity) {
        final CartItem foundCartItem = repositoryCartItems.findById(cartItemId)
                .orElseThrow(() -> new NotFoundCartItem(cartItemId));

        foundCartItem.setQuantity(foundCartItem.getQuantity() - quantity);
        foundCartItem.getProduct().setQuantity(foundCartItem.getProduct().getQuantity() + quantity);

        if (foundCartItem.getQuantity() <= 0) {
            repositoryCartItems.deleteById(foundCartItem.getId());
        }

        repositoryProduct.save(foundCartItem.getProduct());
        repositoryCartItems.save(foundCartItem);
    }

    private static void checkProductStatus(AddToCartRequest cartRequest, Product foundProduct) {
        if (!foundProduct.isAvailable()) {
            throw new UnavailableProduct(foundProduct.getId());
        }
        if (foundProduct.getQuantity() < cartRequest.getQuantity()) {
            throw new InsufficientProductQuantity(foundProduct.getQuantity(), cartRequest.getQuantity());
        }
    }

    private static Cart createNewCartIfDoesNotExist(int quantity, Users currentUser, Product foundProduct) {
        Cart cart = new Cart();
        currentUser.addCart(cart);

        final CartItem item = CartItem.builder()
                .quantity(quantity)
                .build();

        item.addProduct(foundProduct);
        foundProduct.setQuantity(foundProduct.getQuantity() - quantity);
        cart.addItems(item);

        log.info("A new cart created successfully, User id [{}]", currentUser.getId());
        return cart;
    }

    private static Cart createNewItemIfDoesNotExist(int quantity, Cart foundUserCart, Product foundProduct, AtomicReference<ItemStatus> status) {
        final CartItem item = CartItem.builder()
                .quantity(quantity)
                .build();
        item.addProduct(foundProduct);

        foundProduct.setQuantity(foundProduct.getQuantity() - quantity);
        foundUserCart.addItems(item);

        status.set(CREATED);
        log.info("A new cart item created successfully, cart id [{}]", foundUserCart.getId());
        return foundUserCart;
    }

    private static Cart increaseItemQuantityIfExists(int quantity, CartItem item, Product foundProduct, AtomicReference<ItemStatus> status) {
        item.setQuantity(item.getQuantity() + quantity);

        foundProduct.setQuantity(foundProduct.getQuantity() - quantity);

        status.set(INCREASED);
        log.info("Cart item quantity increased successfully, Item id [{}]", item.getId());
        return item.getCart();
    }

    private Product getProduct(Long productId) {
        return repositoryProduct.findById(productId)
                .orElseThrow(() -> new NotFoundProduct(productId));
    }


    private Users getCurrentUserObject(Long userId) {
        return repositoryUser.findById(userId)
                .orElseThrow(() -> new NotFoundUser(userId));
    }
}
