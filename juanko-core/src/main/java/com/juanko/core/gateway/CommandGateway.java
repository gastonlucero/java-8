package com.juanko.core.gateway;

import com.juanko.core.Core;
import com.juanko.core.proxy.CoreGateway;
import com.juanko.core.action.CommandAction;
import com.juanko.core.data.model.RepresentationModel;

import com.juanko.core.proxy.CoreGatewayHandler;
import java.lang.reflect.Proxy;
import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author gaston
 */
public class CommandGateway<C extends CommandAction, T extends RepresentationModel>  implements Observer{

    private static CommandCoreInterface gateway;

    public static CommandCoreInterface getGateway() {
        return gateway;
    }

    public CommandGateway() {       
    }
    @Override
    public void update(Observable o, Object arg) {
         gateway = (CommandCoreInterface) Proxy.newProxyInstance(CoreGateway.class.getClassLoader(),
                new Class[]{CommandCoreInterface.class}, new CoreGatewayHandler((Core) o));
    }
}
