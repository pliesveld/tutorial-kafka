package tutorial.example1;

import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.util.StringUtils;
import tutorial.config.BrokerConfig;
import tutorial.espeak.Espeak;
import tutorial.model.Message;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

@Log4j2
@ShellComponent
public class Consumer {

    @Autowired
    private BrokerConfig brokerConfig;

    @ShellMethod("Subscribe to Kafka topic and execute espeak text-to-speech")
    public String example1_consumer(
            @ShellOption(value = "topic", defaultValue = Producer.TOPIC) String topic,
            @ShellOption(value = "group", defaultValue = "") String group
    ) {
        Properties properties = brokerConfig.consumerProperties();
        if(StringUtils.hasLength(group)) properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, group);

        properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "10");

        KafkaConsumer<String, Message> consumer = new KafkaConsumer<>(properties);

        try (consumer) {
            consumer.subscribe(Arrays.asList(topic));
            while (true) {
                ConsumerRecords<String, Message> records =
                        consumer.poll(Duration.ofMillis(brokerConfig.getEpoll()));

                for (ConsumerRecord<String, Message> record : records) {
                    log.info("Key: " + record.key() + ", Value: " + record.value());
                    log.info("Partition: " + record.partition() + ", Offset:" + record.offset());
                    new Espeak().speak(record.value().getContent());
                }
            }
        }
    }
}
