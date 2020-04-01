package at.taaja.greeneagle;

import io.taaja.messaging.Topics;
import lombok.extern.jbosslog.JBossLog;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

//url: ws://localhost:8091/v1/extensions
@ServerEndpoint("/v1/extensions")
@ApplicationScoped
@JBossLog
public class ExtensionData extends AbstractSocket{

    @OnOpen
    public void onOpen(Session session) {
        this.kafkaDataService.registerClient(session, Topics.SPATIAL_EXTENSION_UPDATE_TOPIC);
        log.info("user attached to extensions");
    }

    @OnClose
    public void onClose(Session session) {
        this.kafkaDataService.removeClient(session, Topics.SPATIAL_EXTENSION_UPDATE_TOPIC);
        log.info("user left extensions");
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        this.kafkaDataService.removeClient(session, Topics.SPATIAL_EXTENSION_UPDATE_TOPIC);
        log.info("user left extension with error " + throwable.getMessage(), throwable);
    }

    @OnMessage
    public void onMessage(String message) {
        // nothing
    }

}
