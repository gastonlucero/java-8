package com.juanko.core.integration.message;

import com.juanko.core.annotations.MessageFromEndPoint;
import com.juanko.core.data.utils.ResourcesManager;

import com.juanko.core.exceptions.ExecutionException;
import com.juanko.core.exceptions.MessageException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Consumer;
import org.apache.camel.Exchange;

/**
 *
 * @author gaston
 */
public abstract class JmsEndPointRoute {

    private static final String MESSAGE_POOL_CONSUMER = "message.consumer";
    private static final String MESSAGE_POOL_MAXCONSUMER = "message.maxConsumer";

    public JmsEndPointRoute() {
        Arrays.asList(this.getClass().getDeclaredMethods()).parallelStream()
                .filter((method) -> {
                    return method.getAnnotation(MessageFromEndPoint.class) != null;
                })
                .forEach((Method method) -> {
                    try {
                        method.invoke(JmsEndPointRoute.this);
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    }
                });       
    }

    public void startEndPoint(String endPointName, Consumer<Exchange> consumer) {
        this.startEndPoint(endPointName, ResourcesManager.getIntegerValue(MESSAGE_POOL_CONSUMER),
                ResourcesManager.getIntegerValue(MESSAGE_POOL_MAXCONSUMER), consumer);
    }

    public void startEndPoint(String endPointName, int consumers, int maxConsumers, Consumer<Exchange> consumer) {
        try {
            MessageFactory.addJmsFromRoute(endPointName, consumers, maxConsumers, consumer);
        } catch (MessageException e) {
           throw new ExecutionException("Error al agregar ruta de inicio", e);
        }
    }

    public SenderMessage endEndPoint(String endPointName) {
        try {
            return MessageFactory.addJmsToRoute(endPointName);
        } catch (MessageException e) {
            throw new ExecutionException("Error al agregar ruta de fin", e);
        }
    }
}
