package at.taaja.greeneagle;

import lombok.extern.jbosslog.JBossLog;

import javax.inject.Inject;
import javax.websocket.OnMessage;
import javax.websocket.Session;

@JBossLog
public abstract class AbstractSocket {

    @Inject
    protected KafkaDataService kafkaDataService;

    @OnMessage
    public void onMessage(String message, Session session) {
        session.getAsyncRemote().sendText("echo: " + message);
    }

}
