package order;

import javax.persistence.*;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import order.exception.CancelException;
import order.external.CoffeeBrew;
import order.external.CoffeeBrewService;
import org.springframework.beans.BeanUtils;

@Entity
@Table(name="Order_table")
@Getter
@Setter
@RequiredArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long orderId;
    private String menu;
    private String status;
    private Integer qty;

    @PostPersist
    public void onPostPersist(){

        Ordered ordered = new Ordered();
        BeanUtils.copyProperties(this, ordered);
        ordered.publishAfterCommit();

    }

    @PreUpdate
    public void onPreUpdate() throws CancelException{

        System.out.println("@@@@@@@@@@@@@@@@@@@@@"+this.getStatus());

        if(this.getStatus().matches("CANCELED|canceled")) {

            CoffeeBrew coffeeBrew = OrderApplication.applicationContext.getBean(CoffeeBrewService.class)
                    .brewStatus(this.orderId);

            if (!coffeeBrew.getStatus().matches("COMPLETED|DELIVERY")) {
                Canceled canceled = new Canceled();
                BeanUtils.copyProperties(this, canceled);
                canceled.publishAfterCommit();
            } else {
                throw new CancelException("주문 상태값 COMPLETED, DELIVERY에서는 취소가 불가능합니다.");
            }
        }
    }

}
