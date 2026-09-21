package krishna.ecommerce.services;

import krishna.ecommerce.dto.product.ProductDetailsResponse;
import krishna.ecommerce.dto.product.ProductRequest;
import krishna.ecommerce.dto.product.ProductResponse;
import krishna.ecommerce.dto.product.ProductUpdateRequest;
import krishna.ecommerce.entity.Product;
import krishna.ecommerce.exception.product.DuplicateProduct;
import krishna.ecommerce.exception.product.ProductNotFoundException;
import krishna.ecommerce.repository.ProductRepository;
import krishna.ecommerce.utility.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // add product
    public ProductResponse createProduct(ProductRequest productRequest) {
        Product product = new Product();
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setCategory(productRequest.getCategory());
        product.setEnabled(true);

        if(productRepository.findByNameAndEnabledTrue(product.getName()).isPresent()) {
            throw new DuplicateProduct("Product with the same name already exists");
        }

        productRepository.save(product);

        ProductResponse response = new ProductResponse();
        response.setName(product.getName());
        response.setMessage("Product created successfully");

        return response;
    }


    // get all the product without sorting
    public PageResponse<ProductDetailsResponse> getProducts(Pageable pageable) {
        Page<Product> products = productRepository.findByEnabledTrue(pageable);

        List<ProductDetailsResponse> allProducts = mapProductsToResponse(products);

        return new PageResponse<>(
                allProducts,
                products.getSize(),
                products.getNumber(),
                products.getTotalElements(),
                products.getTotalPages()
        );
    }

    // get all the products with category-based shorting
    public PageResponse<ProductDetailsResponse> getProducts(Pageable pageable, String category) {
        Page<Product> products = productRepository.findByCategoryAndEnabledTrue(category, pageable);

        List<ProductDetailsResponse> allProducts = mapProductsToResponse(products);

        return new PageResponse<>(
                allProducts,
                products.getSize(),
                products.getNumber(),
                products.getTotalElements(),
                products.getTotalPages()
        );
    }


    // get the product with id
    public ProductDetailsResponse getProductById(Long id){
        // first collect the optional or throw exception
        Optional<Product> optionalProduct = productRepository.findByIdAndEnabledTrue(id);

        // remove the Optional Wrapper and get the product
        Product product = optionalProduct.orElseThrow(() ->
                new ProductNotFoundException("Product Not Found"));

        ProductDetailsResponse response = new ProductDetailsResponse();
        response.setName(product.getName());
        response.setPrice(product.getPrice());
        response.setId(product.getId());
        response.setDescription(product.getDescription());
        response.setCategory(product.getCategory());

        return response;
    }


    // updating the product
    public ProductDetailsResponse updateProduct(
            Long id,
            ProductUpdateRequest request
    ){
        Optional<Product> optionalProduct = productRepository.findByIdAndEnabledTrue(id);
        Product product = optionalProduct.orElseThrow(() ->
                new ProductNotFoundException("Product Not Found for Update"));

        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setDescription(request.getDescription());
        product.setCategory(request.getCategory());

        // this PUT is idempotent in nature so no need to say that already update if
        // a client hit multiple sends
        productRepository.save(product);
        return new ProductDetailsResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getCategory(),
                product.getPrice()
        );
    }


    // soft-deleting the product
    public void deleteProduct(Long id){
        Optional<Product> optionalProduct = productRepository.findById(id);
        Product product = optionalProduct.orElseThrow(() ->
                new ProductNotFoundException("Product Not Found")
        );

        // need this check for the soft deleted product
        if(!product.getEnabled())
            throw new ProductNotFoundException("Product not Found");
        product.setEnabled(false);
        productRepository.save(product);
    }


    // create this method to remove repetitive code
    private List<ProductDetailsResponse> mapProductsToResponse(Page<Product> products){
        return products.map(
                product ->
                        new ProductDetailsResponse(
                                product.getId(),
                                product.getName(),
                                product.getDescription(),
                                product.getCategory(),
                                product.getPrice()
                        )).toList();
    }

}
