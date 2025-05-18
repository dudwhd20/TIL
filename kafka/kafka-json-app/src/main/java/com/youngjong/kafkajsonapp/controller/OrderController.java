package com.youngjong.kafkajsonapp.controller;

import com.youngjong.kafkajsonapp.model.OrderEvent;
import com.youngjong.kafkajsonapp.producer.OrderEventProducer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderEventProducer producer;

    public OrderController(OrderEventProducer producer) {
        this.producer = producer;
    }

    @PostMapping
    public String createOrder(@RequestParam String productId, @RequestParam int quantity){
        OrderEvent event = new OrderEvent(
                UUID.randomUUID().toString(),
                productId,
                quantity,
                LocalDateTime.now()
        );
        producer.sendOrder(event);
        return "Sent:" + event;
    }



}
