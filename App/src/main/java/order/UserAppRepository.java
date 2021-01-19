package order;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserAppRepository extends CrudRepository<UserApp, Long> {

    UserApp findByOrderId(Long orderId);

}