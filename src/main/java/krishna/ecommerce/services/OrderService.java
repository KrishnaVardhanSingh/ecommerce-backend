package krishna.ecommerce.services;

import jakarta.transaction.Transactional;
import krishna.ecommerce.dto.order.OrderDetailsResponse;
import krishna.ecommerce.dto.order.OrderItemResponse;
import krishna.ecommerce.dto.order.OrderResponseDto;
import krishna.ecommerce.dto.order.OrderSummaryResponse;
import krishna.ecommerce.entity.*;
import krishna.ecommerce.exception.cart.CartNotFound;
import krishna.ecommerce.exception.cart.EmptyCart;
import krishna.ecommerce.exception.cart.UserNotFound;
import krishna.ecommerce.exception.inventory.InventoryDoesNotExistException;
import krishna.ecommerce.exception.inventory.QuantityMisMatchException;
import krishna.ecommerce.exception.order.InvalidCancellation;
import krishna.ecommerce.exception.order.OrderNotFound;
import krishna.ecommerce.repository.CartRepository;
import krishna.ecommerce.repository.InventoryRepository;
import krishna.ecommerce.repository.OrderRepository;
import krishna.ecommerce.repository.UserRepository;
import krishna.ecommerce.utility.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryRepository inventoryRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;

    public OrderService(OrderRepository orderRepository,
                        UserRepository userRepository,
                        CartRepository cartRepository,
                        InventoryRepository inventoryRepository){
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.inventoryRepository = inventoryRepository;
    }

    // create order
    @Transactional
    public OrderResponseDto createOrder(String email){
        User user = userRepository.findByEmail(email).orElseThrow(()->{
            return new UserNotFound("User not found");
        });

        Cart cart = cartRepository.findByUserId(user.getId()).orElseThrow(()->{
            return new CartNotFound("Cart not found");
        });

        if(cart.getCartItems().isEmpty()){
            throw new EmptyCart("Cart is empty");
        }

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(BigDecimal.ZERO);

        for(CartItem cartItem : cart.getCartItems()){
            Product product = cartItem.getProduct();
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(product.getPrice());

            BigDecimal itemTotal = orderItem.getUnitPrice()
                    .multiply(BigDecimal.valueOf(orderItem.getQuantity()));

            order.setTotalAmount(order.getTotalAmount().add(itemTotal));
            order.getOrderItems().add(orderItem);

            Inventory inventory =   inventoryRepository.findByProduct(product).orElseThrow(()->
                new InventoryDoesNotExistException("Inventory does not exist")
            );

            int rowModified = inventoryRepository.decreaseStockAtomically(
                inventory.getId(),
                    Math.toIntExact(cartItem.getQuantity())
            );

            if(rowModified == 0){
                throw new QuantityMisMatchException("Insufficient Stock");
            }
        }
        orderRepository.save(order);

        cart.getCartItems().clear();

        OrderResponseDto responseDto = new OrderResponseDto(
                order.getId(),
                order.getStatus(),
                order.getTotalAmount()
        );

        return responseDto;
    }


    // getting the order of a user by order id
    public OrderDetailsResponse getOrderById(Long orderId, String email){
//        User user = userRepository.findById(userId).orElseThrow(() ->
//                new UserNotFound("User not found"));

        Order order = orderRepository.getOrderById(orderId).orElseThrow(() ->
                new OrderNotFound("Order not found"));

        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new UserNotFound("User not found"));

        if(!order.getUser().getId().equals(user.getId())){
            throw new OrderNotFound("Order does not exist");
        }

        return orderDtoMapping(order);
    }

    // canceling an order
    @Transactional
    public OrderDetailsResponse cancelOrder(Long orderId, String email){
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new UserNotFound("User not found"));

        Order order = orderRepository.getOrderById(orderId)
                .orElseThrow(() ->
                        new OrderNotFound("Order with id " + orderId + " not found"));

        if(!order.getUser().getId().equals(user.getId())){
            throw new InvalidCancellation("Invalid cancelation request");
        }

        int rowAffectedInOrderTable = orderRepository.cancelOrder(orderId);

        if (rowAffectedInOrderTable == 0) {
            throw new InvalidCancellation("Invalid cancellation request");
        }

        // we are not storing the item level fulfillment status, so only one status is there for the whole order
        for(OrderItem orderItem : order.getOrderItems()){
            Inventory inventory = inventoryRepository.findByProduct(orderItem.getProduct()).orElseThrow(() ->
                    new InventoryDoesNotExistException("inventory does not exist"));
            int rowAffected = inventoryRepository.increaseStockAtomically(
                    inventory.getId(),
                    Math.toIntExact(orderItem.getQuantity())
            );
            if (rowAffected == 0) {
                throw new InventoryDoesNotExistException(
                        "Inventory could not be restored"
                );
            }
        }



        return orderDtoMapping(order);
    }


    // order dto mapping
    private OrderDetailsResponse orderDtoMapping(Order order){
        List<OrderItem> orderItem = order.getOrderItems();
        List<OrderItemResponse> orderItemResponses = new ArrayList<>();
        for(OrderItem item : orderItem){
            OrderItemResponse itemResponse = new OrderItemResponse(
                    item.getProduct().getId(),
                    item.getProduct().getName(),
                    item.getQuantity(),
                    item.getUnitPrice()
            );
            orderItemResponses.add(itemResponse);
        }

        return new OrderDetailsResponse(
                order.getId(),
                order.getUser().getId(),
                order.getStatus(),
                order.getTotalAmount(),
                orderItemResponses,
                order.getCreatedAt()
        );
    }


    // getting order history in Page
    public PageResponse<OrderSummaryResponse> getOrderHistoryByUserId(String email, Pageable pageable){
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new UserNotFound("User not found"));
        Page<Order> orders = orderRepository.findAllByUserId(user.getId(), pageable);

        List<OrderSummaryResponse> allOrder = mapOrderToResponse(orders);

        return new PageResponse<>(
                allOrder,
                orders.getSize(),
                orders.getNumber(),
                orders.getTotalElements(),
                orders.getTotalPages()
        );
    }

    public PageResponse<OrderSummaryResponse> getOrderHistoryByUserId(String email, Pageable pageable, OrderStatus status){
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new UserNotFound("User not found"));
        Page<Order> orders = orderRepository.findByUserIdAndStatus(user.getId(), pageable, status);

        List<OrderSummaryResponse> allOrder = mapOrderToResponse(orders);

        return new PageResponse<>(
                allOrder,
                orders.getSize(),
                orders.getNumber(),
                orders.getTotalElements(),
                orders.getTotalPages()
        );
    }


    // create this method to remove repetitive code
    private List<OrderSummaryResponse> mapOrderToResponse(Page<Order> orders){
        return orders.map(
                order ->
                        new OrderSummaryResponse(
                                order.getId(),
                                order.getStatus(),
                                order.getTotalAmount(),
                                order.getCreatedAt()
                        )
        ).toList();
    }
}