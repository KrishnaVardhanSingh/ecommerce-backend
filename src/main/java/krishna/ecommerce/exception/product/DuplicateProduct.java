package krishna.ecommerce.exception.product;

public class DuplicateProduct extends RuntimeException{
    public DuplicateProduct(String message) {
        super(message);
    }
}
