package com.Ali.Store.App.checkOut.repository;

import com.Ali.Store.App.dto.checkout.response.OrderItemDetailsDto;
import com.Ali.Store.App.entities.checkout.OrderItem;
import com.Ali.Store.App.entities.checkout.Orders;
import com.Ali.Store.App.entities.productAndCategory.Category;
import com.Ali.Store.App.entities.productAndCategory.Product;
import com.Ali.Store.App.entities.userAndProfileUser.Users;
import com.Ali.Store.App.exceptions.checkout.NotFoundOrder;
import com.Ali.Store.App.repository.checkout.OrderRepository;
import com.Ali.Store.App.repository.checkout.OrderItemsRepository;
import com.Ali.Store.App.repository.productAndCategory.CategoryRepository;
import com.Ali.Store.App.repository.productAndCategory.ProductRepository;
import com.Ali.Store.App.repository.userAndProfileUser.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@DataJpaTest
@ActiveProfiles("test")
public class OrderItemRepositoryTest {


    @Autowired
    private UserRepository repositoryUser;
    @Autowired
    private CategoryRepository repositoryCategory;
    @Autowired
    private ProductRepository repositoryProduct;
    @Autowired
    private OrderRepository repositoryOrder;
    @Autowired
    private OrderItemsRepository repositoryOrderItems;

    private Users savedUser;

    @BeforeEach
    void add_some_products_before_operations() {
        repositoryUser.deleteAll();
        repositoryCategory.deleteAll();
        repositoryProduct.deleteAll();
        repositoryOrder.deleteAll();
        repositoryOrderItems.deleteAll();

        Users user = Users.builder()
                .username("09123456789")
                .createdAt(now())
                .build();
        savedUser = createUserOrder(user);

        createCategoryAndProduct();
    }

    @Test
    void shouldFindUserOrderItemsDetails_whenUserAndOrderExist() {
        final Long userId = savedUser.getId();

        final Orders order = createOrderAndOrderItem(userId);


        final List<OrderItemDetailsDto> userOrderItemsDetails = repositoryOrderItems.findUserOrderItemsDetails(userId, order.getId());
        assertThat(userOrderItemsDetails)
                .extracting(OrderItemDetailsDto::productName, OrderItemDetailsDto::quantity, o -> o.productPrice().intValue())
                .containsExactly(tuple("Fish Stew With Rice", 3, 9000));

    }

    private void createCategoryAndProduct() {
        Product product = Product.builder()
                .name("Fish Stew With Rice")
                .price(new BigDecimal("3000"))
                .quantity(15)
                .isAvailable(true)
                .build();

        Category food = new Category("Food");
        food.addProduct(product);
        repositoryCategory.save(food);
    }

    private Users createUserOrder(Users user) {
        final Orders order = Orders.builder()
                .createdAt(now())
                .build();

        user.addOrder(order);
        return repositoryUser.save(user);
    }

    private Orders createOrderAndOrderItem(Long userId) {
        final Orders order = getOrderByUserId(userId);
        final Product savedProduct = getAllSavedProducts().getFirst();
        final OrderItem orderItem = OrderItem.builder()
                .product(savedProduct)
                .price(savedProduct.getPrice())
                .quantity(3)
                .build();

        order.addItem(orderItem);

        return repositoryOrder.save(order);
    }

    private Orders getOrderByUserId(Long userId) {
        return repositoryOrder.findByUserId(userId)
                .orElseThrow(() -> new NotFoundOrder(userId));
    }


    private List<Product> getAllSavedProducts() {
        return repositoryProduct.findAll();
    }
}
