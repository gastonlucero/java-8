package com.juanko.core.proxy;

import com.juanko.core.Core;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 *
 * @author gaston
 */
public class CoreGatewayHandler implements InvocationHandler {

    private Core core;

    public CoreGatewayHandler(Core core) {
        this.core = core;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = method.invoke(core, args);
        return result;
    }

}
