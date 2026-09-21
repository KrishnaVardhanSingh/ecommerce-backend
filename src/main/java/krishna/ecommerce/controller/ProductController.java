package krishna.ecommerce.controller;

import jakarta.validation.Valid;
import krishna.ecommerce.dto.product.ProductDetailsResponse;
import krishna.ecommerce.dto.product.ProductRequest;
import krishna.ecommerce.dto.product.ProductResponse;
import krishna.ecommerce.dto.product.ProductUpdateRequest;
import krishna.ecommerce.exception.product.InvalidSortingParameter;
import krishna.ecommerce.services.ProductService;
import krishna.ecommerce.utility.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Set;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // adding the product to the DB
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody ProductRequest productRequest
            ){
        ProductResponse productResponse = productService.createProduct(productRequest);
        return ResponseEntity.status(201).body(productResponse);
    }


    // allowed sorting fields
    Set<String> allowedSortField = Set.of(
            "id", "name", "category", "price"
    );


    // fetching all the product with/without sorting
    @GetMapping
    public ResponseEntity<PageResponse<ProductDetailsResponse>> getAllProducts(Pageable pageable
            , @RequestParam(required = false) String category) {
        // validating the sorting fields
        for(Sort.Order order : pageable.getSort()){
            // extracting the sorting field
            String field = order.getProperty();
            if(!allowedSortField.contains((field)))
                    throw new InvalidSortingParameter(
                            "Please give the valid sorting parameter");
        }

        if(category == null){
            return ResponseEntity.ok(productService.getProducts(pageable));
        }
        return ResponseEntity.ok(productService.getProducts(pageable, category));
    }


    // getting the product by ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailsResponse> getProductById(
            @PathVariable Long id){
        return ResponseEntity.ok(productService.getProductById(id));
    }

    // update a product
    @PutMapping("/{id}")
    public ResponseEntity<ProductDetailsResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateRequest request
    ){
        ProductDetailsResponse response = productService.updateProduct(id, request);
        return ResponseEntity.ok(response);
    }


    // soft-deleting product
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(
            @PathVariable Long id
    ){
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}




















