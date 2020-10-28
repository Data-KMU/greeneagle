package at.taaja.greeneagle;

import io.taaja.kafka.Topics;
import lombok.extern.jbosslog.JBossLog;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;



@ServerEndpoint("/v1/extension/{id}")
//@Path("/v1/")
@ApplicationScoped
@JBossLog
public class LifeData extends AbstractSocket{


//    @GET
//    @Produces(MediaType.SERVER_SENT_EVENTS)
//    @SseElementType(MediaType.TEXT_PLAIN)
//    @Path("/extension/{id}")
//    public Multi<String> greetingsAsStream(@PathParam("id") String extensionId) {
//        return this.kafkaDataService.sseStreamForId(this.getTopic(extensionId));
//    }

    @OnOpen
    public void onOpen(Session session, @PathParam("id") String id) {
        this.kafkaDataService.registerClient(session, this.getTopic(id));
        log.info("user attached to id " + id);
    }

    @OnClose
    public void onClose(Session session, @PathParam("id")  String id) {
        this.kafkaDataService.removeClient(session, this.getTopic(id));
        log.info("user left id " + id);
    }

    @OnError
    public void onError(Session session, @PathParam("id") String id, Throwable throwable) {
        this.kafkaDataService.removeClient(session, this.getTopic(id));
        log.info("user left id " + id +" with error " + throwable.getMessage(), throwable);
    }

    private String getTopic(String id){
        return Topics.SPATIAL_EXTENSION_LIFE_DATA_TOPIC_PREFIX + id;
    }

}
