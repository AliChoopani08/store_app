package com.Ali.Store.App.service.checkOut.order;

import com.Ali.Store.App.dto.checkout.response.OrderItemDetailsDto;
import com.Ali.Store.App.dto.checkout.response.UserOrderDetailsDto;
import com.Ali.Store.App.entities.checkout.Cart;
import com.Ali.Store.App.entities.checkout.CartItems;
import com.Ali.Store.App.entities.checkout.OrderItems;
import com.Ali.Store.App.entities.checkout.Orders;
import com.Ali.Store.App.entities.productAndCategory.Product;
import com.Ali.Store.App.entities.userAndProfileUser.Users;
import com.Ali.Store.App.exceptions.user.NotFoundUser;
import com.Ali.Store.App.repository.checkout.RepositoryCart;
import com.Ali.Store.App.repository.checkout.RepositoryCartItems;
import com.Ali.Store.App.repository.checkout.RepositoryOrder;
import com.Ali.Store.App.repository.checkout.RepositoryOrderItems;
import com.Ali.Store.App.repository.productAndCategory.RepositoryProduct;
import com.Ali.Store.App.repository.userAndProfileUser.RepositoryUser;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import static com.Ali.Store.App.service.checkOut.order.OrderStatus.PENDING;


/**
 *Service for managing user's order.
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
public class ServiceOrderImpl implements ServiceOrderInterface{

    private final RepositoryOrder repositoryOrder;
    private final RepositoryUser repositoryUser;
    private final RepositoryProduct repositoryProduct;
    private final RepositoryCartItems repositoryCartItems;
    private final RepositoryCart repositoryCart;
    private final RepositoryOrderItems repositoryOrderItems;


    /**
     * @param userId : The ID of current user
     * @return UserOrderDetailsDto : Display the user's order details as summary the includes <br>
     *                              - Profile user id <br>
     *                              -Order id <br>
     *                              - List of all order-items details of this order
     */
    @Override
    @Transactional
    public UserOrderDetailsDto createOrder(Long userId) {
        // GIVEN
        final Users currentUser = repositoryUser.findById(userId)
                .orElseThrow(() -> new NotFoundUser("This User Is not exist in database !"));

        final Cart userCart = repositoryCart.findByUserId(currentUser.getProfile().getId())
                .orElseThrow(() -> new RuntimeException("This user cart is not exist in database !"));

        final List<Long> productIds = userCart.getCartItems().stream()
                .map(item -> item.getProduct().getId())
                .toList();

        final List<Product> products = repositoryProduct.findAllById(productIds);

        final Map<Long, Product> productIdsMap = products.stream()
                .collect(Collectors.toMap(Product::getId, product -> product));

        int totalPrice = 0;
        List<OrderItems> orderItems = new LinkedList<>();

        for (CartItems item : userCart.getCartItems()) {

            final Product product = productIdsMap.get(item.getProduct().getId());

            OrderItems orderItem = new OrderItems();

            // WHEN
            if(product.getQuantity() == 0) {
                product.setAvailable(false);
            }

            totalPrice += product.getPrice() * item.getQuantity();

            orderItem.setProduct(product);
            orderItem.setPrice(product.getPrice());
            orderItem.setQuantity(item.getQuantity());

            orderItems.add(orderItem);
        }

        Orders order = new Orders();
        order.setTotalPrice(totalPrice);
        order.setOrderStatus(PENDING);
        order.setBidirectionalRelationBetweenOrderAndOrderItems(orderItems);

        currentUser.addOrder(order);

        // THEN
        final Orders savedOrder = repositoryOrder.save(order);
        repositoryProduct.saveAll(products);

        repositoryCartItems.deleteAllByUserId(currentUser.getId());

        final List<OrderItemDetailsDto> orderItemsDetails = repositoryOrderItems.findRequestedUserOrderItemsDetails(currentUser.getProfile().getId(), savedOrder.getId());


        return new UserOrderDetailsDto(currentUser.getProfile().getId(), savedOrder.getId(), orderItemsDetails, totalPrice, order.getOrderStatus());
    }
}
