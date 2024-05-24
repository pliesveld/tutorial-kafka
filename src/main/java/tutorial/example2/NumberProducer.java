package tutorial.example2;

import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.PartitionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import tutorial.config.BrokerConfig;
import tutorial.model.Message;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

@Log4j2
@ShellComponent
public class NumberProducer {

    static final String TOPIC = "example2_group_single";

    @Autowired
    private BrokerConfig brokerConfig;

    @ShellMethod("Publish a number to a topic")
    public String example2_producer(
            @ShellOption(value = "message", defaultValue = "1") String message,
            @ShellOption(value = "topic", defaultValue = NumberProducer.TOPIC) String topic
    ) {
        KafkaProducer<String, Message> producer = new KafkaProducer<>(brokerConfig.producerProperties());

        List<Integer> arrayList = Arrays.asList(message.split(",")).stream().map(Integer::parseInt).collect(Collectors.toList());

        List<Integer> partitions = producer.partitionsFor(topic).stream().map(PartitionInfo::partition).collect(Collectors.toList());
        ListIterator<Integer> it = partitions.listIterator();

        String key = null; // we use null key. Impacts sticky partitioner

        for(int number : arrayList) {
            ProducerRecord<String, Message> producerRecord =
                    new ProducerRecord<>(topic, it.next(), key, Message.builder()
                            .content(String.valueOf(number))
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
                }
            });

            if(!it.hasNext()) it = partitions.listIterator();
            // flush data - synchronous
            producer.flush();
        }

        // flush and close producer
        producer.close();

        return "success";
    }

}
