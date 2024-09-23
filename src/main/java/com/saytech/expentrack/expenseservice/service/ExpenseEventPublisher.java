package com.saytech.expentrack.expenseservice.service;

import com.saytech.expentrack.expenseservice.event.ExpenseEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ExpenseEventPublisher {

    private static final String TOPIC = "notification-topic";

    @Autowired
    private KafkaTemplate<String, ExpenseEvent> kafkaTemplate;

    public void publishExpenseEvent(ExpenseEvent event) {
        kafkaTemplate.send(TOPIC, event);
    }
}
