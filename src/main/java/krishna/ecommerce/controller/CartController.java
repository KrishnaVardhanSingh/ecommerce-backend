package krishna.ecommerce.controller;

import jakarta.validation.Valid;
import krishna.ecommerce.dto.cart.CartDetailsResponse;
import krishna.ecommerce.dto.cart.CartRequest;
import krishna.ecommerce.dto.cart.CartResponse;
import krishna.ecommerce.services.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private CartService cartService;

    public CartController(CartService cartService){
        this.cartService = cartService;
    }

    // adding a product to the cart
    @PostMapping("/items")
    public ResponseEntity<CartResponse> addToCart(@RequestBody @Valid CartRequest request,
                                                  Authentication authentication){
        CartResponse response = cartService.addToCart(request, authentication.getName());
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping
    public ResponseEntity<CartDetailsResponse> getCart(Authentication authentication){
        CartDetailsResponse response = cartService.getCart(authentication.getName());
        return ResponseEntity.ok(response);
    }
}

// But identity should come from authentication.
