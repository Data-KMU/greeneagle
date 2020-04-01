package at.taaja.greeneagle;

import io.taaja.messaging.Topics;
import lombok.extern.jbosslog.JBossLog;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/v1/extension/{id}")
@ApplicationScoped
@JBossLog
public class LifeData extends AbstractSocket{

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

    @OnMessage
    public void onMessage(String message, @PathParam("id") String id) {
        // nothing
    }

    private String getTopic(String id){
        return Topics.SPATIAL_EXTENSION_LIFE_DATA_TOPIC_PREFIX + id;
    }


}
