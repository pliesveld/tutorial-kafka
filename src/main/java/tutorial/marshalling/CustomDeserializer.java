package tutorial.marshalling;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import tutorial.model.Message;

import java.util.Map;

@Log4j2
public class CustomDeserializer implements Deserializer<Message> {
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public Message deserialize(String topic, byte[] data) {
        try {
            if (data == null){
                log.warn("Null received at deserializing");
                return null;
            }
            log.debug("Deserializing...");
            return objectMapper.readValue(new String(data, "UTF-8"), Message.class);
        } catch (Exception e) {
            throw new SerializationException("Error when deserializing byte[] to MessageDto");
        }
    }

    @Override
    public void close() {
    }
}

