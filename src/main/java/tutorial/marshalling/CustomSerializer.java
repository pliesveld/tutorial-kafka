package tutorial.marshalling;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;
import tutorial.model.Message;

import java.util.Map;

@Log4j2
public class CustomSerializer implements Serializer<Message> {
    private final ObjectMapper objectMapper = new ObjectMapper(); // TODO: can dependency injection be used to @Autowired from spring context?

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        objectMapper.findAndRegisterModules();
    }

    @Override
    public byte[] serialize(String topic, Message data) {
        try {
            if (data == null){
                log.debug("Null received at serializing");
                return null;
            }
            log.debug("Serializing...");
            return objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            log.error(e);
            throw new SerializationException("Error when serializing MessageDto to byte[]");
        }
    }

    @Override
    public void close() {
    }
}
