package tutorial;

import com.fasterxml.jackson.databind.JsonDeserializer;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.json.KafkaJsonSchemaSerializerConfig;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import scala.util.control.Exception;
import tutorial.marshalling.CustomDeserializer;
import tutorial.marshalling.CustomSerializer;
import tutorial.model.Message;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Properties;

@Log4j2
@SpringBootApplication
@ShellComponent
public class Application {

    String bootstrapServers = "127.0.0.1:9092";

    @ShellMethod("Change target Kafka server at runtime")
    public String server(
    @ShellOption(value = "arg", defaultValue = "127.0.0.1:9092") String arg
    ) {
        bootstrapServers = arg;
        return bootstrapServers;
    }

    String groupId = "my-fourth-application"; // TODO: move to spring boot property
    String topic = "demo_java"; // TODO: move to spring boot property


    @ShellMethod("Publish Message to Kafka queue")
    public String write( @ShellOption(value = "message", defaultValue = "hello world") String message) {


        // create Producer properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
//        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, io.confluent.kafka.serializers.json.KafkaJsonSchemaSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, CustomSerializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);

        KafkaProducer<String, Message> producer = new KafkaProducer<>(properties);

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


    @ShellMethod("Subscribe to Kafka topic for 10 seconds and print messages")
    public String read() {


        // create Producer properties
        Properties properties = new Properties();

        // create consumer configs
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
//        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, io.confluent.kafka.serializers.json.KafkaJsonSchemaDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, CustomDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true"); // TODO: offload processing dedicated thread to allow loop to poll for messages and use manual committing for strong message delivery guarentees
        properties.setProperty(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, "1000");
        properties.setProperty(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        KafkaConsumer<String, Message> consumer = new KafkaConsumer<>(properties);

        log.debug("Listing topics:");
        consumer.listTopics().keySet().forEach(log::debug);

        // subscribe consumer to our topic(s)
        consumer.subscribe(Arrays.asList(topic));

        // poll for new data

        int i = 1;

        while(true) {
            ConsumerRecords<String, Message> records =
                    consumer.poll(Duration.ofMillis(500));

            log.info("poll event: records count = {}", records.count());
            for (ConsumerRecord<String, Message> record : records) {
                log.info("Key: " + record.key() + ", Value: " + record.value());
                log.info("Partition: " + record.partition() + ", Offset:" + record.offset());
            }

            if(i++ % 30 == 0) break;
        }

        consumer.close(Duration.ofMillis(2000));
        return "success";

    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
