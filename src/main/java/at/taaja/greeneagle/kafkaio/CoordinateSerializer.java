package at.taaja.greeneagle.kafkaio;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.jbosslog.JBossLog;
import org.apache.kafka.common.serialization.Serializer;

@JBossLog
public class CoordinateSerializer implements Serializer {
    @Override
    public byte[] serialize(String topic, Object data) {
        byte[] retVal = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            retVal =  objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return retVal;
    }
}
