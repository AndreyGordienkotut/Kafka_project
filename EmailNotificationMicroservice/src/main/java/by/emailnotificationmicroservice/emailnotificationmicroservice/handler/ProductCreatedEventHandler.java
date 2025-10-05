package by.emailnotificationmicroservice.emailnotificationmicroservice.handler;

import by.emailnotificationmicroservice.core.ProductCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
@KafkaListener(topics = "product-created-events-topic")
@Component
public class ProductCreatedEventHandler {

    private final Logger LOGGER = LoggerFactory.getLogger(ProductCreatedEventHandler.class);


    @KafkaHandler//обрабатывает евент
    public void handle(ProductCreatedEvent productCreatedEvent) {
        LOGGER.info("Reseived event: {}", productCreatedEvent.getTitle());
    }

}
