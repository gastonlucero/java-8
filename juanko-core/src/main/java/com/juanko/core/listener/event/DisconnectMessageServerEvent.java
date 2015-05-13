package com.juanko.core.listener.event;

import com.juanko.core.integration.message.MessageFactory;
import org.apache.log4j.Logger;

/**
 *
 * @author gaston
 */
public class DisconnectMessageServerEvent extends SeagalEvent {

    private static final Logger logger = Logger.getLogger("seagal");

    @Override
    public void handleEvent() {
        try {
            logger.debug("DisconnectEvent enviado desde " + this.getClass().getSimpleName());
            logger.debug("Reconectando servidor de mnesajeria cada 5 segundos");
            while (!MessageFactory.hasStarted()) {
                Thread.sleep(5000);
                MessageFactory.init();
            }
            logger.debug("Servidor de mensajeria iniciado");
        } catch (InterruptedException e) {

        }
    }

}
