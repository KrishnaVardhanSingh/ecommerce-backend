package krishna.ecommerce.controller;

import jakarta.validation.Valid;
import krishna.ecommerce.dto.inventory.InventoryModificationRequest;
import krishna.ecommerce.dto.inventory.InventoryModificationResponse;
import krishna.ecommerce.dto.inventory.InventoryRequest;
import krishna.ecommerce.dto.inventory.InventoryResponse;
import krishna.ecommerce.services.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService){
        this.inventoryService = inventoryService;
    }

    // creating new inventory
    @PostMapping
    public ResponseEntity<InventoryResponse> createInventory(
            @Valid @RequestBody InventoryRequest request
    ){
        InventoryResponse response = inventoryService.createInventory(request);
        return ResponseEntity.status(201).body(response);
    }

    // increase the existing inventory
    @PatchMapping("/{id}/increase")
    public ResponseEntity<InventoryModificationResponse> increaseStock(
            @Valid @RequestBody InventoryModificationRequest request,
            @PathVariable Long id
    ){
        InventoryModificationResponse response = inventoryService.increaseStock(request, id);
        return ResponseEntity.status(200).body(response);
    }

    // decrease the existing stock
    @PatchMapping("/{id}/decrease")
    public ResponseEntity<InventoryModificationResponse> decreaseStock(
            @Valid @RequestBody InventoryModificationRequest request,
            @PathVariable Long id
    ){
        InventoryModificationResponse response = inventoryService.decreaseStock(request, id);
        return ResponseEntity.status(200).body(response);
    }

}




















