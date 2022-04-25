package com.bill.kafka;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.LoggingErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import com.bill.dto.KafkaMsgDto;

import lombok.extern.slf4j.Slf4j;

@EnableKafka
@Configuration
@Slf4j
public class KafkaConfig {

    public static final String TEST_TOPIC = "test";
    public static final String DEFAULT_SERVER = "127.0.0.1:9092";
    public static final String GROUP_1 = "group_1";

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, KafkaMsgDto> userKafkaListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, KafkaMsgDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(userConsumerFactory());
        
        factory.setRetryTemplate(kafkaRetry());
        factory.setRecoveryCallback(retryContext -> {
            ConsumerRecord<String, String> consumerRecord =
                    (ConsumerRecord) retryContext.getAttribute("record");
            log.info("Recovery is called for message: {} ", consumerRecord.value());
            return Optional.empty();
        });
        
        factory.setErrorHandler(errorHandler()); 
        return factory;
    }
    
    @Bean
    public ConsumerFactory<String, KafkaMsgDto> userConsumerFactory() {
        Map<String, Object> config = new HashMap<>();

        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, DEFAULT_SERVER);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_1);
        // use ErrorHandlingDeserializer
        ErrorHandlingDeserializer<KafkaMsgDto> errorHandlingDeserializer =
                new ErrorHandlingDeserializer<>(new JsonDeserializer<>(KafkaMsgDto.class));
        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(),
                errorHandlingDeserializer);
    }
    
    @Bean
    public RetryTemplate kafkaRetry() {
        RetryTemplate retryTemplate = new RetryTemplate();

        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(5 * 1000l);
        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(3);
        retryTemplate.setRetryPolicy(retryPolicy);
        return retryTemplate;
    }

    @Bean
    public LoggingErrorHandler errorHandler() {
        return new LoggingErrorHandler();
    }
}