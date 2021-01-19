package order.external;

import lombok.Getter;
import lombok.Setter;
import order.AbstractEvent;

@Getter
@Setter
public class CoffeeBrew extends AbstractEvent {

    private Long orderId;
    private String menu;
    private String status;
    private Integer qty;

}
