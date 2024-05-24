package tutorial.listeners;

import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import tutorial.model.Message;

import java.util.Collection;
import java.util.List;

/**


 /*
 * The ConsumerRebalanceListener contains callback methods when new partitions are assigned or removed from a consumer.
 * Note: There might be a problem with the following code.  On rebalancing, the partition might be assigned to same node that already processed from the beginning.
 *
 * https://stackoverflow.com/questions/28561147/how-to-read-data-using-kafka-consumer-api-from-beginning
 *
 */
@Log4j2
class PartitionOffsetAssignerListener implements ConsumerRebalanceListener {

    private KafkaConsumer<String, Message> consumer;

    public PartitionOffsetAssignerListener(KafkaConsumer kafkaConsumer) {
        this.consumer = kafkaConsumer;
    }

    @Override
    public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
        for (TopicPartition partition : partitions) {
            log.debug("onPartitionsRevoked Topic: {} Partition: {}", partition.topic(), partition.partition());
        }
    }

    @Override
    public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
        //reading all partitions from the beginning


        for (TopicPartition partition : partitions) {
            log.debug("onPartitionsAssigned Topic: {} Partition: {}", partition.topic(), partition.partition());
            consumer.seekToBeginning(List.of(partition));
        }
    }
}