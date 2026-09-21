package krishna.ecommerce.services;

import krishna.ecommerce.dto.inventory.InventoryModificationRequest;
import krishna.ecommerce.dto.inventory.InventoryModificationResponse;
import krishna.ecommerce.dto.inventory.InventoryRequest;
import krishna.ecommerce.dto.inventory.InventoryResponse;
import krishna.ecommerce.entity.Inventory;
import krishna.ecommerce.entity.Product;
import krishna.ecommerce.exception.inventory.InventoryAlreadyExistsException;
import krishna.ecommerce.exception.inventory.InventoryDoesNotExistException;
import krishna.ecommerce.exception.inventory.QuantityMisMatchException;
import krishna.ecommerce.exception.product.ProductNotFoundException;
import krishna.ecommerce.repository.InventoryRepository;
import krishna.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;


import java.util.Optional;

@Service
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    public InventoryService(InventoryRepository inventoryRepository,
                            ProductRepository productRepository){
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
    }

    // creating inventory
    public InventoryResponse createInventory(InventoryRequest request){
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() ->
                        new ProductNotFoundException("Product not found"
                ));

        Optional<Inventory> inventoryOptional = inventoryRepository.findByProduct(product);
        if(inventoryOptional.isPresent()){
            throw new InventoryAlreadyExistsException("Product already exist in inventory");
        }

        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setQuantity(request.getQuantity());

        inventoryRepository.save(inventory);

        return new InventoryResponse(
                inventory.getProduct().getId(),
                "Product added successfully"
        );
    }


    // increasing the stock
    public InventoryModificationResponse increaseStock(
            InventoryModificationRequest request,
            Long id
    ){
        Inventory inventory = inventoryRepository.findById(id).orElseThrow(()->
                new InventoryDoesNotExistException("Product with this id does not exist in Inventory"));

        inventory.setQuantity(inventory.getQuantity() + request.getQuantity());
        inventoryRepository.save(inventory);
        return new InventoryModificationResponse("Product increased successfully");
    }

    // decreasing the stock
    public InventoryModificationResponse decreaseStock(
            InventoryModificationRequest request,
            Long id
    ){
        Inventory inventory = inventoryRepository.findById(id).orElseThrow(() ->
                new InventoryDoesNotExistException("Product with this id does not exist in Inventory"));

        if(request.getQuantity() > inventory.getQuantity()){
            throw new QuantityMisMatchException("Enter Valid Quantity");
        }
        inventory.setQuantity(inventory.getQuantity() - request.getQuantity());
        inventoryRepository.save(inventory);
        return new InventoryModificationResponse("Product decreased successfully");
    }

}
