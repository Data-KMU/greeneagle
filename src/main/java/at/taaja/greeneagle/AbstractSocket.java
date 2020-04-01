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


}
