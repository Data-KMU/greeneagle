package at.taaja.greeneagle;

import io.taaja.Constants;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/v1/corridor/{id}")
@ApplicationScoped
public class LifeCorridorDataSocket extends AbstractSocket{

    @Override
    protected String getPrefix() {
        return Constants.KAFKA_CORRIDOR_TOPIC_PREFIX;
    }
}
