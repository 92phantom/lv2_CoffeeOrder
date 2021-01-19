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
        System.err.println("@@@ LOGGER: (Delivery_PolicyHandler)" + eventString);

    }

    @Autowired
    DeliveryRepository deliveryRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverBrewCompleted_(@Payload BrewCompleted brewCompleted){

        if(brewCompleted.isMe()){
            System.err.println("##### wheneverBrewCompleted_  : " + brewCompleted.toJson());

            Delivery delivery =  deliveryRepository.findByOrderId(brewCompleted.getOrderId());

            if(delivery == null){
                delivery = new Delivery();
            }
            else{
                delivery.setId(delivery.getId());
            }

            delivery.setMenu(brewCompleted.getMenu());
            delivery.setOrderId(brewCompleted.getOrderId());
            delivery.setQty(brewCompleted.getQty());
            delivery.setStatus(brewCompleted.getStatus());

            debug(delivery);

            deliveryRepository.save(delivery);

        }
    }

    public void debug(Delivery delivery){

        System.err.println("----------------------");
        System.err.println(delivery.getMenu());
        System.err.println(delivery.getOrderId());
        System.err.println(delivery.getQty());
        System.err.println(delivery.getStatus());
        System.err.println("----------------------");

    }

}
