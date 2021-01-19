package order;

import order.config.kafka.KafkaProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import order.UserApp.*;

@Service
public class UserAppViewHandler {

    @StreamListener(KafkaProcessor.INPUT)
    public void onStringEventListener(@Payload String eventString) {
        System.err.println("@@@ LOGGER: (UserApp_ViewHandler)" + eventString);
    }

    @Autowired
    private UserAppRepository userAppRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOrdered_then_CREATE_1(@Payload Ordered ordered) {
        if (ordered.isMe()) {

            System.err.println("##### wheneverOrdered_then_CREATE_1  : " + ordered.toJson());

            UserApp userApp = userAppRepository.findByOrderId(ordered.getOrderId());

            if (userApp == null) {
                userApp = new UserApp();
            }

            userApp.setMenu(ordered.getMenu());
            userApp.setOrderId(ordered.getOrderId());
            userApp.setQty(ordered.getQty());
            userApp.setStatus(ordered.getStatus());

            userAppRepository.save(userApp);
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverBrewCompleted_UPDATE_1(@Payload BrewCompleted brewCompleted) {

        if (brewCompleted.isMe()) {
            System.err.println("##### wheneverBrewCompleted_listener  : " + brewCompleted.toJson());

            UserApp userApp = userAppRepository.findByOrderId(brewCompleted.getOrderId());

            // UPDATE STATUS
            userApp.setStatus(brewCompleted.getStatus());

            // order status : COMPLETED
            userAppRepository.save(userApp);
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverDeliveryComplete_UPDATE_1(@Payload DeliveryComplete deliveryComplete) {

        if (deliveryComplete.isMe()) {
            System.err.println("##### wheneverDeliveryComplete_ listener  : " + deliveryComplete.toJson());

            UserApp userApp = userAppRepository.findByOrderId(deliveryComplete.getOrderId());

            userApp.setStatus(deliveryComplete.getStatus());

            userAppRepository.save(userApp);
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverCanceled_UPDATE_1(@Payload Canceled canceled) {

        if (canceled.isMe()) {
            System.err.println("##### wheneverCanceled_listener  : " + canceled.toJson());

            UserApp coffeeBrew = userAppRepository.findByOrderId(canceled.getOrderId());
            coffeeBrew.setStatus("CANCELED");

            userAppRepository.save(coffeeBrew);
        }
    }

}