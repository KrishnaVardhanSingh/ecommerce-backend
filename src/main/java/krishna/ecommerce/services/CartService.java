package krishna.ecommerce.services;

import krishna.ecommerce.dto.cart.CartDetailsResponse;
import krishna.ecommerce.dto.cart.CartItemResponse;
import krishna.ecommerce.dto.cart.CartRequest;
import krishna.ecommerce.dto.cart.CartResponse;
import krishna.ecommerce.entity.*;
import krishna.ecommerce.exception.cart.CartNotFound;
import krishna.ecommerce.exception.cart.UserNotFound;
import krishna.ecommerce.exception.inventory.InventoryDoesNotExistException;
import krishna.ecommerce.exception.inventory.QuantityMisMatchException;
import krishna.ecommerce.exception.product.ProductNotFoundException;
import krishna.ecommerce.repository.*;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final UserRepository userRepository;

    public CartService(CartItemRepository cartItemRepository,
                       CartRepository cartRepository,
                       ProductRepository productRepository,
                       InventoryRepository inventoryRepository,
                       UserRepository userRepository){
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.userRepository = userRepository;
    }


    // adding item to the cart
    public CartResponse addToCart(CartRequest request,
                                  Long userId){
        // user check
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFound("User Not Found"));

        // product check
        Optional<Product> optionalProduct = productRepository.findByIdAndEnabledTrue(request.getProductId());
        Product product = optionalProduct.orElseThrow(() ->
                new ProductNotFoundException("Product Not Found"));

        // quantity check
        Inventory inventory = inventoryRepository.findByProduct(product)
                .orElseThrow(() -> new InventoryDoesNotExistException("Inventory Not Found"));

        if(request.getQuantity() > inventory.getQuantity()){
            throw new QuantityMisMatchException("Insufficient Storage");
        }


        // cart check
        Optional<Cart> optionalCart = cartRepository.findByUserId(userId);
        Cart cart = optionalCart.orElseGet(() ->{
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });

        // cart + requested should be <= total in inventory
        Optional<CartItem> optionalCartItem = cartItemRepository.findByCartIdAndProductId(
                cart.getId(),
                product.getId()
        );


        // if existed, then check the quantity again
        if(optionalCartItem.isPresent()){
            if((optionalCartItem.get().getQuantity() + request.getQuantity()) > inventory.getQuantity()){
                throw new QuantityMisMatchException("Insufficient Storage");
            }
            CartItem cartItem = optionalCartItem.get();
            cartItem.setQuantity(
                    cartItem.getQuantity() + request.getQuantity()
            );
            cartItemRepository.save(cartItem);

        }else {

            // if cartItem does not exist
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(request.getQuantity());

            cartItemRepository.save(cartItem);
        }

        return new CartResponse("Item added to cart successfully");
    }


    // getting cart of the user
    public CartDetailsResponse getCart(Long userId){
        // finding user
        Optional<User> optionalUser = userRepository.findById(userId);

        User user = optionalUser.orElseThrow(() ->
            new UserNotFound("User Not Found")
        );

        // finding cart
        Optional<Cart> optionalCart = cartRepository.findByUserId(userId);
        Cart cart = optionalCart.orElseThrow(() ->
                new CartNotFound("Cart not found"));

        // fetching the cartItems
        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
        CartDetailsResponse cartDetailsResponse = new CartDetailsResponse();
        BigDecimal total = BigDecimal.ZERO;
        for(CartItem c : cartItems){
            CartItemResponse cartItemResponse = new CartItemResponse();
            cartItemResponse.setProductId(c.getProduct().getId());
            cartItemResponse.setProductName(c.getProduct().getName());
            cartItemResponse.setQuantity(c.getQuantity());
            cartItemResponse.setPrice(c.getProduct().getPrice());
            // BigDecimal multiplication
            cartItemResponse.setItemTotal(
                    c.getProduct().getPrice().multiply(BigDecimal.valueOf(c.getQuantity()))
            );

            cartDetailsResponse.getItems().add(cartItemResponse);
            cartDetailsResponse.setCartTotal(total.add(cartItemResponse.getItemTotal()));
        }

        return cartDetailsResponse;
    }
}
