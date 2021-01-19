package order;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class DeliveryComplete extends AbstractEvent {

    private Long orderId;
    private String menu;
    private String status;
    private Integer qty;

}
