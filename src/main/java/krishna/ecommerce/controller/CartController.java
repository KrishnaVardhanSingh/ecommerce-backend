package krishna.ecommerce.controller;

import jakarta.validation.Valid;
import krishna.ecommerce.dto.cart.CartDetailsResponse;
import krishna.ecommerce.dto.cart.CartRequest;
import krishna.ecommerce.dto.cart.CartResponse;
import krishna.ecommerce.services.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private CartService cartService;

    public CartController(CartService cartService){
        this.cartService = cartService;
    }

    // adding a product to the cart
    @PostMapping("/{userId}/items")
    public ResponseEntity<CartResponse> addToCart(@RequestBody @Valid CartRequest request,
                                                  @PathVariable Long userId){
        CartResponse response = cartService.addToCart(request, userId);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<CartDetailsResponse> getCart(@Valid @PathVariable Long userId){
        CartDetailsResponse response = cartService.getCart(userId);
        return ResponseEntity.status(200).body(response);
    }
}
