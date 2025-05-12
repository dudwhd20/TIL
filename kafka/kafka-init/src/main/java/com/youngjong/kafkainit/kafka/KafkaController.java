package com.youngjong.kafkainit.kafka;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/send")
public class KafkaController {

    private final KafkaProducerService producerService;


    public KafkaController(KafkaProducerService producerService) {
        this.producerService = producerService;
    }

    @PostMapping
    public String sendMessage(@RequestParam String message){
        producerService.sendMessage("spring-test", message);
        return "Message sent: " + message;
    }

}
