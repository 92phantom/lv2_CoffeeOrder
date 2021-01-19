package order;

import order.config.kafka.KafkaProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class PolicyHandler{
    @StreamListener(KafkaProcessor.INPUT)
    public void onStringEventListener(@Payload String eventString){

        System.err.println("@@@ LOGGER: (BREW_PolicyHandler)" + eventString);

    }

    @Autowired
    CoffeeBrewRepository coffeeBrewRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOrdered_(@Payload Ordered ordered){

        if(ordered.isMe()){

            System.err.println("##### wheneverOrdered_listener  : " + ordered.toJson());

            CoffeeBrew coffeeBrew =  coffeeBrewRepository.findByOrderId(ordered.getOrderId());

            if(coffeeBrew == null){
                coffeeBrew = new CoffeeBrew();
            }
            else{
                coffeeBrew.setId(coffeeBrew.getId());
            }

            coffeeBrew.setOrderId(ordered.getOrderId());
            coffeeBrew.setQty(ordered.getQty());
            coffeeBrew.setMenu(ordered.getMenu());

            // Set Brew Status : Watting - 조리 전
            coffeeBrew.setStatus("WAITTING");
            coffeeBrewRepository.save(coffeeBrew);
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverCanceled_(@Payload Canceled canceled){

        if(canceled.isMe()){
            System.err.println("##### wheneverCanceled_listener  : " + canceled.toJson());

            CoffeeBrew coffeeBrew = coffeeBrewRepository.findByOrderId(canceled.getOrderId());
            coffeeBrew.setStatus("CANCELED");

            // 일부 수량만 취소 불가능하기 때문에 별도로 setQty하지 않음.
            coffeeBrewRepository.save(coffeeBrew);
        }
    }

}
