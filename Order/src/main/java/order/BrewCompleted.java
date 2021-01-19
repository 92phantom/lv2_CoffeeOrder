
package order;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class BrewCompleted extends AbstractEvent {


    private Long id;
    private Long orderId;
    private String menu;
    private String status;
    private Integer qty;

}
