package krishna.ecommerce.exception.order;

public class InvalidCancellation extends RuntimeException {
  public InvalidCancellation(String message) {
    super(message);
  }
}
