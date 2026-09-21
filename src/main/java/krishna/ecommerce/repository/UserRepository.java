package krishna.ecommerce.repository;

import jakarta.validation.constraints.Email;
import krishna.ecommerce.entity.User;
import org.hibernate.boot.models.JpaAnnotations;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
