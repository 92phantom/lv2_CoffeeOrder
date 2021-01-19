
package order.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name="CoffeeBrew", url="${api.coffeeBrew.url}")
public interface CoffeeBrewService {

    @RequestMapping(method= RequestMethod.GET, path="/coffeeBrews/{orderId}")
    public CoffeeBrew brewStatus(@PathVariable("orderId") final Long orderId);

}