package order;

import javax.persistence.*;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

@Entity
@Table(name="CoffeeBrew_table")
@Getter
@Setter
@RequiredArgsConstructor
public class CoffeeBrew {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private Long orderId;
    private String menu;
    private String status;
    private Integer qty;

    @PostUpdate
    public void onPostUpdate(){

        BrewCompleted brewCompleted = new BrewCompleted();
        BeanUtils.copyProperties(this, brewCompleted);

        System.err.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+this.getStatus());

        brewCompleted.publishAfterCommit();
    }

}
