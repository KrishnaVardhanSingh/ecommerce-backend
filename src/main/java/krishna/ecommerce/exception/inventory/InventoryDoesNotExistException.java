package krishna.ecommerce.exception.inventory;

public class InventoryDoesNotExistException extends RuntimeException {
    public InventoryDoesNotExistException(String message) {
        super(message);
    }
}
