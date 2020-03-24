package at.taaja.greeneagle;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/v1/zones/{id}")
@ApplicationScoped
public class LifeSpatialExtensionSocket extends AbstractSocket{

    @ConfigProperty(name = "kafka.prefix.zone")
    private String zonePrefix;

    @Override
    protected String getPrefix() {
        return this.zonePrefix;
    }
}
