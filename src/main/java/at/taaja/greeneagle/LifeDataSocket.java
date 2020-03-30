package at.taaja.greeneagle;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.server.ServerEndpoint;

//url: ws://localhost:8091/v1/data/c56b3543-6853-4d86-a7bc-1cde673a5582
@ServerEndpoint("/v1/data/{id}")
@ApplicationScoped
public class LifeDataSocket extends AbstractSocket{

    @ConfigProperty(name = "kafka.prefix.vehicle-data")
    private String vehicleDataPrefix;

    @Override
    protected String getPrefix() {
        return this.vehicleDataPrefix;
    }
}
