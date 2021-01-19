package order;

import order.config.kafka.KafkaProcessor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PolicyHandler{
    @StreamListener(KafkaProcessor.INPUT)
    public void onStringEventListener(@Payload String eventString){

        System.err.println("@@@ LOGGER: (ORDER_PolicyHandler)" + eventString);

    }

    @Autowired
    OrderRepository orderRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverBrewCompleted_(@Payload BrewCompleted brewCompleted){

        if(brewCompleted.isMe()){
            System.err.println("##### wheneverBrewCompleted_listener  : " + brewCompleted.toJson());

            Long brew_id = brewCompleted.getOrderId();
            Order order = orderRepository.findByOrderId(brew_id);

            // UPDATE STATUS
            order.setStatus(brewCompleted.getStatus());

            // order status : COMPLETED
            orderRepository.save(order);
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverDeliveryComplete_(@Payload DeliveryComplete deliveryComplete){

        if(deliveryComplete.isMe()){
            System.err.println("##### wheneverDeliveryComplete_ listener  : " + deliveryComplete.toJson());

            Long delivery_id = deliveryComplete.getOrderId();
            Order order = orderRepository.findByOrderId(delivery_id);

            order.setStatus(deliveryComplete.getStatus());

            orderRepository.save(order);
        }
    }

}
