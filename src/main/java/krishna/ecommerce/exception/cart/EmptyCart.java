package krishna.ecommerce.exception.cart;

public class EmptyCart extends RuntimeException {
    public EmptyCart(String message) {
        super(message);
    }
}
