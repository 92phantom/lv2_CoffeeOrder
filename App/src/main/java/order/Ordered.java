package order;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@RequiredArgsConstructor
public class Ordered extends AbstractEvent {

    private Long orderId;
    private String menu;
    private String status;
    private Integer qty;

}
