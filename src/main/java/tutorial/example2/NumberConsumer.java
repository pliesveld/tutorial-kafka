package tutorial.example2;

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
import tutorial.listeners.LogPartitionListener;
import tutorial.model.Message;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

@Log4j2
@ShellComponent
public class NumberConsumer {

    @Autowired
    private BrokerConfig brokerConfig;

    @Autowired
    private Espeak espeak;

    @ShellMethod("Subscribe to Kafka topic and execute espeak text-to-speech")
    public String example2_consumer(
            @ShellOption(value = "topic", defaultValue = NumberProducer.TOPIC) String topic,
            @ShellOption(value = "group", defaultValue = "") String group,
            @ShellOption(value = "max_poll_records", defaultValue = "1") String max_poll_records
    ) {
        Properties properties = brokerConfig.consumerProperties();
        if(StringUtils.hasLength(group)) properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, group);
        if(StringUtils.hasLength(max_poll_records)) properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, max_poll_records);

        KafkaConsumer<String, Message> consumer = new KafkaConsumer<>(properties);

        try (consumer) {
            consumer.subscribe(Arrays.asList(topic), new LogPartitionListener(consumer));

            int total = 0;

            while (true) {
                ConsumerRecords<String, Message> records =
                        consumer.poll(Duration.ofMillis(brokerConfig.getEpoll()));

                boolean messageFound = false;
                for (ConsumerRecord<String, Message> record : records) {
                    messageFound = true;
                    log.info("Key: " + record.key() + ", Value: " + record.value());
                    log.info("Partition: " + record.partition() + ", Offset:" + record.offset());
                    int record_int = Integer.valueOf(record.value().getContent());
                    total += record_int;
                }

                if(messageFound) {
                    espeak.speak(String.format("total is %d", total));
                }
            }
        }
    }
}


