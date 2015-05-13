package com.juanko.core.integration.message;

import com.juanko.core.Core;
import com.juanko.core.data.utils.ResourcesManager;

import com.juanko.core.exceptions.MessageException;
import com.juanko.core.listener.event.DisconnectMessageServerEvent;
import java.util.function.Consumer;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQPrefetchPolicy;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.component.jms.JmsEndpoint;
import org.apache.camel.impl.DefaultCamelContext;

/**
 *
 * @author gaston
 */
public final class MessageFactory {

    private static CamelContext camelContext;
    private static ActiveMQConnectionFactory connectionFactory;

    private static final String BROKER_URL = "activemq.brokerUrl";

    public static void init() {
        try {
            camelContext = new DefaultCamelContext();
            connectionFactory = new ActiveMQConnectionFactory(ResourcesManager.getPropertyValue(BROKER_URL));
            ActiveMQPrefetchPolicy prefetch = new ActiveMQPrefetchPolicy();
            prefetch.setQueuePrefetch(1000);
            connectionFactory.setPrefetchPolicy(prefetch);
            camelContext.addComponent("activeMq",
                    JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
        } catch (Exception e) {
            Core.sendEvent(new DisconnectMessageServerEvent());
        }
    }

    public static boolean hasStarted() {
        return camelContext != null;
    }

    public static void start() {
        try {
            camelContext.start();
        } catch (Exception e) {
           Core.sendEvent(new DisconnectMessageServerEvent());
        }
    }

    public static void addJmsFromRoute(String endPoint, int consumers, int maxConsumers, Consumer process) throws MessageException {
        if (camelContext == null) {
            init();
        }
        try {
            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(getJmsEndPoint(endPoint, consumers, maxConsumers))
                            .autoStartup(true).routeId(endPoint)
                            .process((exchange) -> {
                                process.accept(exchange);
                            }).end();
                }
            });
        } catch (Exception e) {
            Core.sendEvent(new DisconnectMessageServerEvent());
            throw new MessageException("Error agregando route al servidor de mensajeria", e);
        }
    }

    public static SenderMessage addJmsToRoute(String endPoint) throws MessageException {
        if (camelContext == null) {
            init();
        }
        try {
            ProducerTemplate producer = camelContext.createProducerTemplate();
            JmsEndpoint toEndPoint = getJmsEndPoint(endPoint, 1, 1);
            SenderMessage senderFunction = (message) -> {
                try{
                    producer.sendBody(toEndPoint, message);
                }catch(Exception e){
                    e.printStackTrace();
                }
            };
            return senderFunction;
        } catch (Exception e) {
            Core.sendEvent(new DisconnectMessageServerEvent());
            throw new MessageException("Error agregando route al servidor de mensajeria", e);
        }
    }

    private static JmsEndpoint getJmsEndPoint(String endPoint, int consumers, int maxConsumers) {
        JmsEndpoint queue = (JmsEndpoint) camelContext.getEndpoint(endPoint);
        queue.setConnectionFactory(connectionFactory);
        queue.setConcurrentConsumers(consumers);
        queue.setMaxConcurrentConsumers(maxConsumers);
        queue.setAcknowledgementMode(Session.AUTO_ACKNOWLEDGE);
        return queue;
    }
}
