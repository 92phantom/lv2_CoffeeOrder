package order;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface CoffeeBrewRepository extends PagingAndSortingRepository<CoffeeBrew, Long>{

    CoffeeBrew findByOrderId(Long orderId);
}