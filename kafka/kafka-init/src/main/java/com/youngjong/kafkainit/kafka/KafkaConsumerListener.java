package com.youngjong.kafkainit.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumerListener {

    @KafkaListener(topics = "spring-test", groupId = "my-group")
    public void listen(ConsumerRecord<String, String> record){
        System.out.println("[Consumer] Received: " + record.value());
    }
}
