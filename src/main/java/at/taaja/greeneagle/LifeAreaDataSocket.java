package at.taaja.greeneagle;

import io.taaja.Constants;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.server.ServerEndpoint;

//url: ws://localhost:8091/v1/data/c56b3543-6853-4d86-a7bc-1cde673a5582
@ServerEndpoint("/v1/area/{id}")
@ApplicationScoped
public class LifeAreaDataSocket extends AbstractSocket{

    @Override
    protected String getPrefix() {
        return Constants.KAFKA_AREA_TOPIC_PREFIX;
    }
}
