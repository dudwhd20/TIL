package com.youngjong.kafkajsonapp.consumer;

import com.youngjong.kafkajsonapp.model.OrderEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderEventConsumer {

    @KafkaListener(topics =  "order-events", groupId = "order-group")
    public void consume(OrderEvent orderEvent){
        System.out.println("Received Order: " + orderEvent);
    }

}
