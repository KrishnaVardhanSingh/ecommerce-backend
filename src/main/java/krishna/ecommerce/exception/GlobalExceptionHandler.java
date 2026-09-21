package krishna.ecommerce.exception;

import krishna.ecommerce.exception.cart.CartNotFound;
import krishna.ecommerce.exception.cart.UserNotFound;
import krishna.ecommerce.exception.inventory.InventoryAlreadyExistsException;
import krishna.ecommerce.exception.inventory.InventoryDoesNotExistException;
import krishna.ecommerce.exception.inventory.QuantityMisMatchException;
import krishna.ecommerce.exception.product.DuplicateProduct;
import krishna.ecommerce.exception.product.InvalidSortingParameter;
import krishna.ecommerce.exception.product.ProductNotFoundException;
import krishna.ecommerce.exception.user.EmailAlreadyExist;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CartNotFound.class)
    public ResponseEntity<ErrorResponse> handleCartNotFoundExistException(
            CartNotFound e
    ){
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrors(
                List.of(e.getMessage())
        );

        errorResponse.setTimestamp(System.currentTimeMillis());
        errorResponse.setStatus(404);

        return ResponseEntity.status(404).body(errorResponse);
    }

    @ExceptionHandler(EmailAlreadyExist.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExistException(
            EmailAlreadyExist e
    ){
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrors(
                List.of(e.getMessage())
        );

        errorResponse.setTimestamp(System.currentTimeMillis());
        errorResponse.setStatus(409);

        return ResponseEntity.status(409).body(errorResponse);
    }

    @ExceptionHandler(UserNotFound.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(
            UserNotFound e
    ){
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrors(
                List.of(e.getMessage())
        );

        errorResponse.setTimestamp(System.currentTimeMillis());
        errorResponse.setStatus(404);

        return ResponseEntity.status(404).body(errorResponse);
    }

    @ExceptionHandler(QuantityMisMatchException.class)
    public ResponseEntity<ErrorResponse> handleQuantityMisMatchException(
            QuantityMisMatchException e
    ){
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrors(
                List.of(e.getMessage())
        );

        errorResponse.setTimestamp(System.currentTimeMillis());
        errorResponse.setStatus(409);

        return ResponseEntity.status(409).body(errorResponse);
    }

    @ExceptionHandler(InventoryDoesNotExistException.class)
    public ResponseEntity<ErrorResponse> handleInventoryDoesNotExist(
            InventoryDoesNotExistException e
    ){
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrors(
                List.of(e.getMessage())
        );

        errorResponse.setTimestamp(System.currentTimeMillis());
        errorResponse.setStatus(404);

        return ResponseEntity.status(404).body(errorResponse);
    }

    @ExceptionHandler(InventoryAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateInventoryEntry(
            InventoryAlreadyExistsException e
    ){
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrors(
                List.of(e.getMessage())
        );

        errorResponse.setTimestamp(System.currentTimeMillis());
        errorResponse.setStatus(409);

        return ResponseEntity.status(409).body(errorResponse);
    }

    @ExceptionHandler(InvalidSortingParameter.class)
    public ResponseEntity<ErrorResponse> handleSortingValidationException(
            InvalidSortingParameter e){
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrors(
                List.of(e.getMessage())
        );
        errorResponse.setStatus(400);
        errorResponse.setTimestamp(System.currentTimeMillis());

        return ResponseEntity.status(400).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrors(
                ex.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(error -> error.getField() + ": " + error.getDefaultMessage())
                        .toList()
        );
        errorResponse.setStatus(400);
        errorResponse.setTimestamp(System.currentTimeMillis());
        return ResponseEntity.status(400).body(errorResponse);
    }

    @ExceptionHandler(DuplicateProduct.class)
    public ResponseEntity<ErrorResponse> handleDuplicateProductException(
            DuplicateProduct ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrors(
                List.of(ex.getMessage())
        );
        errorResponse.setStatus(409);
        errorResponse.setTimestamp(System.currentTimeMillis());
        return ResponseEntity.status(409).body(errorResponse);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFoundException(
            ProductNotFoundException ex) {

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrors(
                List.of(ex.getMessage())
        );
        errorResponse.setStatus(404);
        errorResponse.setTimestamp(System.currentTimeMillis());
        return ResponseEntity.status(404).body(errorResponse);
    }
}
