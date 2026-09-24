package krishna.ecommerce.repository;

import krishna.ecommerce.entity.Order;
import krishna.ecommerce.entity.OrderStatus;
import krishna.ecommerce.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> getOrderById(Long orderId);

    Page<Order> findAllByUserId(Long userId, Pageable pageable);
    Page<Order> findByUserIdAndStatus(Long userId, Pageable pageable, OrderStatus status);

    @Modifying(clearAutomatically = true)
    @Query("""
            UPDATE Order o
            SET o.status = 'CANCELED'
            WHERE o.id = :orderId
            AND o.status IN ('PENDING', 'CONFIRMED')
            """)
    int cancelOrder(@Param("orderId") Long orderId);
}
