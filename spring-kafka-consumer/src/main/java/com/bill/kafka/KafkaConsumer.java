package com.bill.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.bill.dto.KafkaMsgDto;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class KafkaConsumer {
	
    @KafkaListener(topics = KafkaConfig.TEST_TOPIC, groupId = KafkaConfig.GROUP_1,
            containerFactory = "userKafkaListenerFactory")
    public void consumeJson(KafkaMsgDto dto) throws InterruptedException {
    	log.info("Consumed JSON Message: {} ", dto);
    	
    	String message = dto.getMessage();
    	if ("retry".equals(message)) {
    		log.info("receive Retry message ...");
    		throw new RuntimeException("receive Retry message ...");
    	}
    	log.info("Consumed done. ");
    }
}
