package com.Ali.Store.App.service.checkOut.order;

import com.Ali.Store.App.dto.checkout.response.OrderItemDetailsDto;
import com.Ali.Store.App.dto.checkout.response.UserOrderDetailsDto;
import com.Ali.Store.App.entities.checkout.Cart;
import com.Ali.Store.App.entities.checkout.OrderItem;
import com.Ali.Store.App.entities.checkout.Orders;
import com.Ali.Store.App.entities.productAndCategory.Product;
import com.Ali.Store.App.entities.userAndProfileUser.Users;
import com.Ali.Store.App.exceptions.user.NotFoundUser;
import com.Ali.Store.App.repository.checkout.CartRepository;
import com.Ali.Store.App.repository.checkout.CartItemsRepository;
import com.Ali.Store.App.repository.checkout.OrderRepository;
import com.Ali.Store.App.repository.checkout.OrderItemsRepository;
import com.Ali.Store.App.repository.userAndProfileUser.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import static com.Ali.Store.App.service.checkOut.order.OrderStatus.PENDING;
import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;

/**
 * Service for managing user's order.
 * <p>Purpose:<br>
 * This Service implements the logic for placing an order
 * for user
 * </p>
 *
 * <p>Workflow:<br>
 * 1. Identify logged-in user using @AuthenticationPrincipal.<br>
 * 2. Fetch the logged-in user's cart from database.<br>
 * 3. Set all products in the cart in an individual OrderItem.<br>
 * 4. Set all OrderItems in user's order.<br>
 * 5. Change the status of this order to PEND.<br>
 * 6. Save the newly created order in database.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository repositoryOrder;
    private final UserRepository repositoryUser;
    private final CartItemsRepository repositoryCartItems;
    private final CartRepository repositoryCart;
    private final OrderItemsRepository repositoryOrderItems;


    /**
     * @param userId : The ID of current user
     * @return UserOrderDetailsDto : Display the user's order details as summary the includes <br>
     * - Profile user id <br>
     * -Order id <br>
     * - List of all order-items details of this order
     */
    @Override
    @Transactional
    public UserOrderDetailsDto createOrder(Long userId) {
        final Users currentUser = getUser(userId);
        final Cart cart = getUserCart(currentUser);

        final BigDecimal totalPrice = getTotalPrice(cart);
        final List<OrderItem> orderItems = createOrderItems(cart);

        Orders order = Orders.builder().totalPrice(totalPrice).orderStatus(PENDING).build();

        orderItems.forEach(order::addItem);
        currentUser.addOrder(order);

        final Orders savedOrder = repositoryOrder.save(order);
        repositoryCartItems.deleteAllByCartId(currentUser.getCart().getId());


        final List<OrderItemDetailsDto> orderItemsDetails = repositoryOrderItems.findUserOrderItemsDetails(currentUser.getId(), savedOrder.getId());

        return new UserOrderDetailsDto(currentUser.getId(), savedOrder.getId(), orderItemsDetails, totalPrice, order.getOrderStatus());
    }

    private Users getUser(Long userId) {
        return repositoryUser.findById(userId)
                .orElseThrow(() -> new NotFoundUser(userId));
    }

    private Cart getUserCart(Users currentUser) {
        return repositoryCart.findByUserId(currentUser.getId()).orElseThrow(() -> new RuntimeException("This user cart is not exist in database !"));
    }

    private static BigDecimal getTotalPrice(Cart cart) {
        return cart.getCartItems().stream().map(i -> {
            final Product product = i.getProduct();

            return (product != null) ? product.getPrice().multiply(valueOf(i.getQuantity())) : ZERO;
        }).reduce(ZERO, BigDecimal::add);
    }

    private List<OrderItem> createOrderItems(Cart cart) {
        return cart.getCartItems().stream()
                .map(i -> {
            final Product product = i.getProduct();

            final OrderItem item = OrderItem.builder()
                    .price(product.getPrice())
                    .quantity(i.getQuantity())
                    .build();
            item.addProduct(product);

            return item;
        }).toList();
    }
}
