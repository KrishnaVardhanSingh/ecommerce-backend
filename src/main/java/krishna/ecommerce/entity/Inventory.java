package krishna.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DialectOverride;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "inventory",
        check = @CheckConstraint(constraint = "quantity>=0")
)
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Long quantity;
}
