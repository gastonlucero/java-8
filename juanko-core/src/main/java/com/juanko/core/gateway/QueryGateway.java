package com.juanko.core.gateway;

import com.juanko.core.Core;
import com.juanko.core.proxy.CoreGateway;
import com.juanko.core.action.QueryAction;
import com.juanko.core.data.model.RepresentationModel;

import com.juanko.core.proxy.CoreGatewayHandler;
import java.lang.reflect.Proxy;
import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author gaston
 */
public class QueryGateway<Q extends QueryAction, R extends RepresentationModel> implements Observer {

    private static QueryCoreInterface gateway;

    public static QueryCoreInterface getGateway() {
        return gateway;
    }

    public QueryGateway() {
    }

    @Override
    public void update(Observable o, Object arg) {
        gateway = (QueryCoreInterface) Proxy.newProxyInstance(CoreGateway.class.getClassLoader(),
                new Class[]{QueryCoreInterface.class}, new CoreGatewayHandler((Core) o));
    }
}
