package at.taaja.greeneagle;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import lombok.extern.jbosslog.JBossLog;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.websocket.Session;
import java.time.Duration;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@ApplicationScoped
@JBossLog
public class KafkaDataService {


    @ConfigProperty(name = "kafka.bootstrap-servers")
    private String bootstrapServers;

    @ConfigProperty(name = "kafka.poll-records")
    private int pollRecords;

    @ConfigProperty(name = "kafka.auto-commit")
    private boolean autoCommit;

    @ConfigProperty(name = "kafka.offset-reset")
    private String offsetReset;

//     topic -> Queue of Sessions
    private Map<String, Queue<Session>> connections = new ConcurrentHashMap<>();
    private Consumer<Long, String> kafkaConsumer;
    private boolean isRunning = false;
    private AtomicBoolean subscriberChanged = new AtomicBoolean(false);
    private Thread poller;

    public void registerClient(Session session, String topic){
        //is topic already subscribed
        if(connections.containsKey(topic)){
            this.connections.get(topic).add(session);
        }else{
            Queue<Session> sessions = new ConcurrentLinkedQueue();
            sessions.add(session);
            this.connections.put(topic,sessions);
            this.updateConsumerList();
            if(!isRunning){
                isRunning = true;
                this.startPolling();
            }
        }
    }

    public void removeClient(Session session, String topic){
        if(!this.connections.containsKey(topic)){
            return;
        }

        Queue<Session> sessions = this.connections.get(topic);
        sessions.remove(session);

        if(sessions.isEmpty()){
            //unsubscribe
            this.connections.remove(topic);
            this.updateConsumerList();
        }
    }

    private void updateConsumerList(){
        this.subscriberChanged.set(true);
    }


    //init kafka
    void onStart(@Observes StartupEvent ev) {
        final Properties consumerProperties = new Properties();
        consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, this.bootstrapServers);
        consumerProperties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, this.pollRecords);
        consumerProperties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, this.autoCommit);
        consumerProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, this.offsetReset);

        String uuid = UUID.randomUUID().toString();
        consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, "group-" + uuid);
        consumerProperties.put(ConsumerConfig.CLIENT_ID_CONFIG, "client-" + uuid);
        this.kafkaConsumer = new KafkaConsumer(consumerProperties, new LongDeserializer(), new StringDeserializer());
        log.info("kafka created");

    }

    private void startPolling(){
        this.poller = new Thread(() -> {
            while(isRunning) {
                if(this.subscriberChanged.getAndSet(false)){
                    this.kafkaConsumer.subscribe(this.connections.keySet());
                }
                ConsumerRecords<Long, String> records = this.kafkaConsumer.poll(Duration.ofMillis(100));
                for(ConsumerRecord<Long, String> record : records){
                    Queue<Session> sessions = this.connections.get(record.topic());
                    if(sessions == null) {
                        log.error("received unknown topic: " + record.topic());
                    }else{
                        sessions.forEach(s -> s.getAsyncRemote().sendObject(record.value(), result ->  {
                            Throwable exception = result.getException();
                            if (exception != null) {
                                log.warn("Unable to send message: " + exception.getMessage(), exception);
                            }
                        }));
                    }
                }
            }
        });
        this.poller.setName("poll thread");
        this.poller.start();
    }

    void onStop(@Observes ShutdownEvent ev) {
        log.info("shutdown kafka");
        this.isRunning = false;
        this.kafkaConsumer.close();
    }

}
