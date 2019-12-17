package at.taaja.greeneagle.handler;

import at.taaja.greeneagle.config.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("socketHandler")
public class SocketHandler extends AbstractWebSocketHandler {

    private Logger logger = LoggerFactory.getLogger(SocketHandler.class);

    private Map<String, WebSocketSession> webSocketSessions = new HashMap();

    /**
     * bin: https://kafka.apache.org/downloads
     * tutorial: http://cloudurable.com/blog/kafka-tutorial-kafka-from-command-line/index.html
     * bash cmd: bin/kafka-console-producer.sh --broker-list visit.mkay.at:9092 --topic posUpdate
     *
     * @param message
     */
    @KafkaListener(topics = Constants.TOPIC_VEHICLE_UPDATE)
    public void posUpdate(String message){
        logger.info(String.format("Vehicle update %s",message));

        this.webSocketSessions.forEach((s, webSocketSession) -> {
            try {
                webSocketSession.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                logger.warn("cant send message to websocket", e);
                e.printStackTrace();
            }
        });

    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        logger.info("handleBinaryMessage", message);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        logger.info("handleMessage", message.toString());

        if(! this.webSocketSessions.containsKey(session.getId())){
            this.webSocketSessions.put(session.getId(), session);
        }

        WebSocketMessage wsm = new TextMessage("Hallo Test");
        session.sendMessage(wsm);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        this.webSocketSessions.remove(session.getId());
    }

}
