package at.taaja.greeneagle;

import io.reactivex.Emitter;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.subscription.MultiEmitter;
import io.smallrye.mutiny.subscription.MultiSubscriber;
import io.taaja.services.AbstractKafkaConsumerService;
import lombok.extern.jbosslog.JBossLog;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.Session;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

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



    @Override
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

//todo

//    public Multi<String> sseStreamForId(final String extensionId) {
//        return Multi.createFrom().items(new Supplier<Stream<? extends String>>() {
//            @Override
//            public Stream<? extends String> get() {
//                return null;
//            }
//        }) items(blockingQueue.stream());
//    }

}
