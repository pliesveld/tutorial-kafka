package tutorial.example1;

import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import tutorial.config.BrokerConfig;
import tutorial.model.Message;

import java.time.LocalDateTime;

@Log4j2
@ShellComponent
public class Producer {

    static final String TOPIC = "example1";

    @Autowired
    private BrokerConfig brokerConfig;

    @ShellMethod("Publish message to Kafka queue")
    public String example1_producer(
            @ShellOption(value = "message", defaultValue = "hello world") String message,
            @ShellOption(value = "topic", defaultValue = Producer.TOPIC) String topic
    ) {

        KafkaProducer<String, Message> producer = new KafkaProducer<>(brokerConfig.producerProperties());

        ProducerRecord<String, Message> producerRecord =
                new ProducerRecord<>(topic, Message.builder()
                        .content(message)
                        .timestamp(LocalDateTime.now())
                        .build());

        // send data - asynchronous
        producer.send(producerRecord, new Callback() {
            public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                // executes every time a record is successfully sent or an exception is thrown
                if (e == null) {
                    // the record was successfully sent
                    log.info("Received new metadata. \n" +
                            "Topic:" + recordMetadata.topic() + "\n" +
                            "Partition: " + recordMetadata.partition() + "\n" +
                            "Offset: " + recordMetadata.offset() + "\n" +
                            "Timestamp: " + recordMetadata.timestamp());
                } else {
                    log.error("Error while producing", e);
                }
            }});

        // flush data - synchronous
        producer.flush();

        // flush and close producer
        producer.close();

        return "success";
    }

}
