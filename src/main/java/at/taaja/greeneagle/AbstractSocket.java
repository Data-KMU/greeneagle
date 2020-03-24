package at.taaja.greeneagle;

import lombok.extern.jbosslog.JBossLog;

import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.server.PathParam;
import javax.websocket.Session;

@JBossLog
public abstract class AbstractSocket {

    @Inject
    protected KafkaDataService kafkaDataService;

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
        return this.getPrefix() + "-" + id;
    }

    protected abstract String getPrefix();

}
