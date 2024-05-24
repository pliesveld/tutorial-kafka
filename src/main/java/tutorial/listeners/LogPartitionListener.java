package tutorial.listeners;

import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import tutorial.model.Message;

import java.util.Collection;
import java.util.List;

/*
 *  Simple listener to log when partitions are assigned and revoked
 */
@Log4j2
public class LogPartitionListener implements ConsumerRebalanceListener {

    private KafkaConsumer consumer;

    public LogPartitionListener(KafkaConsumer kafkaConsumer) {
        this.consumer = kafkaConsumer;
    }

    @Override
    public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
        String group = consumer.groupMetadata().groupId();
        for (TopicPartition partition : partitions) {
            log.debug("onPartitionsRevoked Group: {} Topic: {} Partition: {}", group, partition.topic(), partition.partition());
        }


    }

    @Override
    public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
        String group = consumer.groupMetadata().groupId();
        for (TopicPartition partition : partitions) {
            log.debug("onPartitionsAssigned Group: {} Topic: {} Partition: {}", group, partition.topic(), partition.partition());
        }
    }
}