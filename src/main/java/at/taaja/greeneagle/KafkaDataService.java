package at.taaja.greeneagle;

import io.taaja.services.AbstractKafkaConsumerService;
import lombok.extern.jbosslog.JBossLog;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.Session;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@ApplicationScoped
@JBossLog
public class KafkaDataService extends AbstractKafkaConsumerService {

//     topic -> Queue of Sessions
    private Map<String, Queue<Session>> connections = new ConcurrentHashMap<>();

    public void registerClient(Session session, String topic){
        //is topic already subscribed
        if(connections.containsKey(topic)){
            this.connections.get(topic).add(session);
        }else{
            Queue<Session> sessions = new ConcurrentLinkedQueue();
            sessions.add(session);
            this.connections.put(topic,sessions);

        }
    }

    public void removeClient(Session session, String topic){
        if(!this.connections.containsKey(topic)){
            return;
        }

        Queue<Session> sessions = this.connections.get(topic);
        sessions.remove(session);

        if(sessions.isEmpty()){
            this.connections.remove(topic);
        }
    }

    protected void processRecord(final ConsumerRecord<String, String> consumerRecord) {

        Queue<Session> sessions = this.connections.get(consumerRecord.topic());

        if(sessions != null){
            sessions.forEach(s -> s.getAsyncRemote().sendObject(consumerRecord.value(), result ->  {
                Throwable exception = result.getException();
                if (exception != null) {
                    log.warn("Unable to send message: " + exception.getMessage(), exception);
                }
            }));

        }

    }

    /**
     * Use clientId in groupId to prevent clustering
     * @param clientId
     * @param groupName
     * @return
     */
    @Override
    protected String getGroupId(String clientId, String groupName) {
        return groupName + "/" + clientId;
    }



//todo: create SSE endpoint
//
//    public Multi<String> sseStreamForId(final String extensionId) {
//        return Multi.createFrom().items(new Supplier<Stream<? extends String>>() {
//            @Override
//            public Stream<? extends String> get() {
//                return null;
//            }
//        }) items(blockingQueue.stream());
//    }

}
