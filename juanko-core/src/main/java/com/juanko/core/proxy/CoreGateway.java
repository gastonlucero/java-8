package com.juanko.core.proxy;

import com.juanko.core.Core;
import com.juanko.core.dao.PublicDaoManager;
import java.lang.reflect.Proxy;
import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author gaston
 */
public class CoreGateway implements Observer {

    private static CoreInterface gateway;

    public CoreGateway() {

    }

//    public CoreGateway(final Core core) {
//        coreInterface = (CoreInterface) Proxy.newProxyInstance(CoreGateway.class.getClassLoader(),
//                new Class[]{CoreInterface.class,
//                    PublicDaoManager.class}, new CoreGatewayHandler(core));
//    }

    public static CoreInterface getGateway() {
        return gateway;
    }

    @Override
    public void update(Observable o, Object arg) {
         gateway = (CoreInterface) Proxy.newProxyInstance(CoreGateway.class.getClassLoader(),
                new Class[]{CoreInterface.class,
                    PublicDaoManager.class}, new CoreGatewayHandler((Core)o));
    }

}
