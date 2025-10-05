package by.emailnotificationmicroservice.emailnotificationmicroservice.handler;

import by.emailnotificationmicroservice.core.ProductCreatedEvent;
import by.emailnotificationmicroservice.emailnotificationmicroservice.exception.NonRetryableException;
import by.emailnotificationmicroservice.emailnotificationmicroservice.exception.RetryableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@KafkaListener(topics = "product-created-events-topic")
@Component
public class ProductCreatedEventHandler {

    private final Logger LOGGER = LoggerFactory.getLogger(ProductCreatedEventHandler.class);
    private RestTemplate restTemplate;
    public ProductCreatedEventHandler(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @KafkaHandler//обрабатывает евент
    public void handle(ProductCreatedEvent productCreatedEvent) {
//        if(true){
//            throw new NonRetryableException("Non retryable exception");
//        }
        LOGGER.info("Reseived event: {}", productCreatedEvent.getTitle());

        //это создание рест запроса другого сервиса(я не делал его) по логике что если будет ошибка ResourceAccessException то ее нужно повторить,
        // а если полетел сервер то выдать неповторяемую ошибку(просто для примера логики)
        String url = "http://localhost:8090";
        try{
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET,null,String.class);
            if(response.getStatusCode().value()== HttpStatus.OK.value()){
                LOGGER.info("Received response: {}", response.getBody());
            }
        }
        catch (ResourceAccessException e){
            LOGGER.error(e.getMessage());
            throw new RetryableException(e);
        }catch (HttpServerErrorException e){
            LOGGER.error(e.getMessage());
            throw new NonRetryableException(e);
        }catch (Exception e){
            LOGGER.error(e.getMessage());
            throw new NonRetryableException(e);
        }
    }

}
