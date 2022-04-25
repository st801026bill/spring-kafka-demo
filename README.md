**一. 環境安裝** 
---
**1. 下載與安裝kafka**    
[下載 Apache Kafka](https://kafka.apache.org/downloads)，請安裝binary版本不要用Source版本  
此專案是用kafka_2.12-3.1.0、JDK 11當範例  
※ Kafka 所有的執行檔放置於 bin 資料夾下，由於筆者環境是 Windows 系統，執行的是 bin/windows 路徑下的 bat 檔。

**2. 啟動kafka**  
Step 1：開起新的 Terminal，啟動 Zookeeper，成功啟動看到 Zookeeper 的位址。  
```sql
> zookeeper-server-start.bat ../../config/zookeeper.properties
```
Step 2：開啟新的 Terminal，啟動 Kafka Server，成功啟動會看到 Kafka Server 的位址 localhost:9092、broker.id=0
```sql
> kafka-server-start.bat ../../config/server.properties
```

**3：測試發佈與訂閱**  
**這兩個步驟完成後，系統中就有 Kafka 在運行了。接下來簡單測試一下 Kafka 的運作方式：**  
Step 1：開起薪的 Terminal，新增 Topic test  
```sql
> kafka-topics.bat --create --bootstrap-server localhost:9092 --topic test --replication-factor 1 --partitions 1
```
Step 2：列出 Topic test 詳情  
```sql
> kafka-topics.bat --describe --bootstrap-server localhost:9092 --topic test
```
Step 3：在 Terminal 分頁各別執行 Producer 和 Consumer 發佈和訂閱 Topic test，一切就緒後在 Producer 的 Terminal 輸入訊息送出，Consumer 的 Terminal 會自動出現該訊息。  
```sql
> kafka-console-producer.bat --bootstrap-server localhost:9092 --topic test
```
```sql
> kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic test
```

**二. Spring 實作與執行** 
---
Step 1:
啟動 Zookeeper 與 Kafka Server  
以及 SpringKafkaProducerApplication、SpringKafkaConsumerApplication

Step2:
進入swagger介面 `http://localhost:8888/kafka-producer/swagger-ui/index.html#/ProducerController/post`  
並執行 "kafka 發布訊息" API
![image](https://github.com/st801026bill/spring-kafka-demo/blob/master/swagger.png)
![image](https://github.com/st801026bill/spring-kafka-demo/blob/master/consumer_log.png)

**三. 例外與錯誤處理** 
---
**1. 毒藥訊息 : 因consumer 反序列化異常導致**
  - 使用ErrorHandlingDeserializer處理，避免進入死循環
  ```sql
  ```
**2. 發佈後處理**
  - Producer : callback處理
  ```sql
  ```
  - Consumer : Retry機制
  ```sql
  ```
