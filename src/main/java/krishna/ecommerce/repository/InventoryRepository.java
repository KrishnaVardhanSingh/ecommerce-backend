package krishna.ecommerce.repository;

import krishna.ecommerce.entity.Inventory;
import krishna.ecommerce.entity.Product;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByProduct(Product product);

    // atomic decrease operation
    @Modifying
    @Query("""
    UPDATE Inventory i
    SET i.quantity = i.quantity - :requestedQuantity
    WHERE i.id = :inventoryId
    AND i.quantity >= :requestedQuantity
""")
    int decreaseStockAtomically(@Param("inventoryId") Long inventoryId,
                                @Param("requestedQuantity") int requestedQuantity);


    @Modifying
    @Query("""
    UPDATE Inventory i
    SET i.quantity = i.quantity + :requestedQuantity
    WHERE i.id = :inventoryId
""")
    int increaseStockAtomically(
            @Param("inventoryId") Long inventoryId,
            @Param("requestedQuantity") int requestQuantity
    );


}


// @Query(...) --> "Spring, don't derive the query from the method name. I'm explicitly telling you what query to execute."

// @Modifying --> "This query is a modifying query, such as UPDATE or DELETE, rather than a SELECT."

//           @Query
//              ↓
//   "What query should I execute?"
//
//          @Modifying
//              ↓
//    "This query changes data."
//
//           @Param
//              ↓
// "Which Java argument goes into which named query parameter?"