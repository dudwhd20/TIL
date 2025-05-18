package com.youngjong.kafkajsonapp.producer;

import com.youngjong.kafkajsonapp.model.OrderEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderEventProducer {


    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;


    public OrderEventProducer(KafkaTemplate<String, OrderEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    public void sendOrder(OrderEvent orderEvent){
        kafkaTemplate.send("order-events", orderEvent.getOrderId(), orderEvent);
    }


}
