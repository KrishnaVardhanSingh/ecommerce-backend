package krishna.ecommerce.exception.inventory;

public class QuantityMisMatchException extends RuntimeException {
    public QuantityMisMatchException(String message) {
        super(message);
    }
}
