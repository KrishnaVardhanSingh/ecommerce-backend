package krishna.ecommerce.dto.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailsResponse {
    private Long id;
    private String name;
    private String description;
    private String category;
    private BigDecimal price;
}
