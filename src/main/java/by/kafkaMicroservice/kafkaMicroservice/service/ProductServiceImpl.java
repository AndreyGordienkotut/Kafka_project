package by.kafkaMicroservice.kafkaMicroservice.service;

import by.emailnotificationmicroservice.core.ProductCreatedEvent;
import by.kafkaMicroservice.kafkaMicroservice.dto.CreateProductDto;

import jdk.jfr.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.kafka.support.SendResult;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


@Service
public class ProductServiceImpl implements ProductService {
    private KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public ProductServiceImpl(KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public String createProduct(CreateProductDto createProductDto) throws ExecutionException, InterruptedException {
        //TODO save DB
        String productId = UUID.randomUUID().toString();
        ProductCreatedEvent productCreatedEvent = new
                ProductCreatedEvent(productId,createProductDto.getTitle(),createProductDto.getPrice(),createProductDto.getQuantity());

        //асинхронное отправление
        CompletableFuture<org.springframework.kafka.support
                .SendResult<String,ProductCreatedEvent>> future = kafkaTemplate
                .send("product-created-events-topic",productId,productCreatedEvent);
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                LOGGER.error("Failed : {}", ex.getMessage());
            }else{
                LOGGER.info("Created product : {}", result.getRecordMetadata());
            }
        });
        //синхронное отправление
        SendResult<String,ProductCreatedEvent> result = kafkaTemplate
                .send("product-created-events-topic",productId,productCreatedEvent).get();
        LOGGER.info("Topic : {}", result.getRecordMetadata().topic());
        LOGGER.info("Partition : {}", result.getRecordMetadata().partition());


        LOGGER.info("Return : {}", productId);
        return productId;
    }
}
