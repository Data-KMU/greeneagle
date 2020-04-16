package at.taaja.greeneagle;

import lombok.extern.jbosslog.JBossLog;

import javax.inject.Inject;

@JBossLog
public abstract class AbstractSocket {

    @Inject
    protected KafkaDataService kafkaDataService;


}
