package krishna.ecommerce.exception.cart;

public class CartNotFound extends RuntimeException {
    public CartNotFound(String message){
        super(message);
    }
}
