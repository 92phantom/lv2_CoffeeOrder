package order;

import javax.persistence.*;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import java.util.List;

@Entity
@Table(name="Delivery_table")
@Setter
@Getter
@RequiredArgsConstructor
public class Delivery {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private Long orderId;
    private String menu;
    private String status;
    private Integer qty;


    @PostUpdate
    public void onPostUpdate(){

        DeliveryComplete deliveryComplete = new DeliveryComplete();
        BeanUtils.copyProperties(this, deliveryComplete);
        deliveryComplete.publishAfterCommit();

    }

}
