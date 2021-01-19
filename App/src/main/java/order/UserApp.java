package order;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="UserApp_table")
@Setter
@Getter
@RequiredArgsConstructor
public class UserApp {

        @Id
        @GeneratedValue(strategy=GenerationType.AUTO)
        private Long id;
        private Long orderId;
        private String status;
        private String menu;
        private Integer qty;

}
