package at.taaja.greeneagle.handler;

import at.taaja.greeneagle.config.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

public class Socket implements WebSocketHandler {

    Logger logger = LoggerFactory.getLogger(Socket.class);

    @KafkaListener(topics = Constants.TOPIC_VEHICLE_UPDATE)
    public void posUpdate(String message){
        logger.info(String.format("Vehicle update %s",message));
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
        logger.debug("afterConnectionEstablished", webSocketSession);
    }

    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {
        logger.debug("handleMessage", webSocketMessage);
    }

    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {
        logger.debug("handleTransportError", throwable);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        logger.debug("afterConnectionClosed", closeStatus);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
