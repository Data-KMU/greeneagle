package at.taaja.greeneagle.kafkaio;

import io.quarkus.runtime.StartupEvent;
import io.taaja.blueracoon.model.Coordinates;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongSerializer;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import java.util.Properties;
import java.util.UUID;

@ApplicationScoped
public class ConsumerService {

    private static Producer<Long, Coordinates> kafkaProducer;

//    @ConfigProperty(name = "kafka.bootstrap-servers")
    public String bootstrapServers = "46.101.136.244:9092";

//    @ConfigProperty(name = "kafka.topic")
    public String topic = "sensor";

    synchronized void onStart(@Observes StartupEvent ev) {
        Properties producerProperties = new Properties();
        producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        producerProperties.put(ProducerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());

        this.kafkaProducer = new KafkaProducer<Long, Coordinates>(producerProperties, new LongSerializer(), new CoordinateSerializer());
    }


    public void publishCoordinates(Coordinates coordinates){
        this.kafkaProducer.send(
            new ProducerRecord<>(
                    this.topic,
                    coordinates
            )
        );
    }
}
