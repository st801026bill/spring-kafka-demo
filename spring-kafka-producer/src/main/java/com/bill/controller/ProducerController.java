package com.bill.controller;

import javax.validation.Valid;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaProducerException;
import org.springframework.kafka.core.KafkaSendCallback;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bill.dto.KafkaMsgDto;
import com.bill.dto.PositionMessageReqDto;
import com.bill.kafka.KafkaConfig;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("kafka")
@Tag(name = "ProducerController", description = "Kafka Producer管理服務")
@Slf4j
public class ProducerController {
	
	@Autowired
    private KafkaTemplate<String, KafkaMsgDto> kafkaTemplate;
	
	//參考範例: https://www.tpisoftware.com/tpu/articleDetails/2518
	/*
	 * 	調整 KafkaConfig Timeout 數值
	 * 	config.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 1);
	 * 	config.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 1);
	 * 	讓 Failure Callback 觸發
	 */
	/*
	 * 	Success case:
	 * 	{
	 * 		"ID": "1",
	 * 		"Message": "1"
	 *	}
	 *
	 *	position-message case(consumer reflect fails)
	 * 	{
	 * 		"ID": "postion-message",
	 * 		"Message": "1"
	 *	}
	 *	
	 *	retry case
	 * 	{
	 * 		"ID": "1",
	 * 		"Message": "retry"
	 *	}
	 */
	@Operation(summary = "kafka 發布訊息", description = "kafka 發布訊息")
	@PostMapping("/produce")
	public String post(@Valid @RequestBody PositionMessageReqDto reqDto) {
		KafkaMsgDto kafkaMsgDto = new KafkaMsgDto(reqDto.getId(), reqDto.getMessage());
		log.info("KafkaMsgDto : {}", kafkaMsgDto.toString());
		
		ListenableFuture<SendResult<String, KafkaMsgDto>> future = 
				kafkaTemplate.send(KafkaConfig.TEST_TOPIC, kafkaMsgDto);
				
		future.addCallback(new KafkaSendCallback<String, KafkaMsgDto>() {
            @Override
            public void onSuccess(SendResult<String, KafkaMsgDto> result) {
                log.info("success send message:{} with offset:{} ", kafkaMsgDto,
                        result.getRecordMetadata().offset());
            }

            @Override
            public void onFailure(KafkaProducerException ex) {
            	log.error("fail send message! Do somthing....");
            	log.error("ex: {}", ex.getMessage());
            }
        });
		
		return "Published done";
	}
}
