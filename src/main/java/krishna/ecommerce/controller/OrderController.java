package krishna.ecommerce.controller;

import krishna.ecommerce.dto.order.OrderDetailsResponse;
import krishna.ecommerce.dto.order.OrderResponseDto;
import krishna.ecommerce.dto.order.OrderSummaryResponse;
import krishna.ecommerce.entity.OrderStatus;
import krishna.ecommerce.entity.User;
import krishna.ecommerce.exception.product.InvalidSortingParameter;
import krishna.ecommerce.services.OrderService;
import krishna.ecommerce.utility.PageResponse;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;
import java.util.Set;

@RestController
@RequestMapping("/api/order")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService){
        this.orderService = orderService;
    }

    // create an order / checkout
    @PostMapping("/{userId}")
    public ResponseEntity<OrderResponseDto> createOrder(@PathVariable Long userId){
        OrderResponseDto response = orderService.createOrder(userId);
        return ResponseEntity.status(201).body(response);
    }

    // get order by orderId
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailsResponse> getOrderById(
            @PathVariable Long orderId
    ){
        OrderDetailsResponse response = orderService.getOrderById(orderId);
        return ResponseEntity.status(200).body(response);
    }


    // cancel order
    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<OrderDetailsResponse> cancelOrder(
            @PathVariable Long orderId
    ){
        OrderDetailsResponse response = orderService.cancelOrder(orderId);
        return ResponseEntity.status(200).body(response);
    }

    // get all the order history
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "createdAt",
            "totalAmount",
            "id",
            "status"
    );
    @GetMapping("/user/{userId}")
    public ResponseEntity<PageResponse<OrderSummaryResponse>> getOrderHistory(@PathVariable Long userId,
                                                                              Pageable pageable,
                                                                              @RequestParam(required = false) OrderStatus status){
        for(Sort.Order order : pageable.getSort()){
            String field = order.getProperty();
            if(!ALLOWED_SORT_FIELDS.contains(field)){
                throw new InvalidSortingParameter(
                        "Please give the valid sorting parameter");
            }
        }
        if(status == null)
            return ResponseEntity.ok(orderService.getOrderHistoryByUserId(userId, pageable));
        return ResponseEntity.ok(orderService.getOrderHistoryByUserId(userId, pageable, status));
    }
}