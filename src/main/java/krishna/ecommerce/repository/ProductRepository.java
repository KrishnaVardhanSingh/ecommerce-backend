package krishna.ecommerce.repository;

import krishna.ecommerce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByNameAndEnabledTrue(String name);

    // findBy + field + And + field + True

    Page<Product> findByEnabledTrue(Pageable pageable);

    Page<Product> findByCategoryAndEnabledTrue(
            String category,
            Pageable pageable
    );

    Optional<Product> findByIdAndEnabledTrue(Long id);
}
