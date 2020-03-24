package at.taaja.greeneagle;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.server.ServerEndpoint;

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
