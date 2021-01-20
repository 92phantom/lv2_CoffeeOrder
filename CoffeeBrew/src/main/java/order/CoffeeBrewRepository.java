package order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface CoffeeBrewRepository extends JpaRepository<CoffeeBrew, Long> {

    CoffeeBrew findByOrderId(Long orderId);

    Optional<CoffeeBrew> findById(Long orderId);

}