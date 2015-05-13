package com.juanko.core;

import com.juanko.core.listener.event.CoreEventsListener;
import com.juanko.core.proxy.CoreInterface;

/**
 *
 * @author gaston
 */
public interface CoreBase extends CoreEventsListener, CoreInterface{

    default void initControllers() {
        addControllers();
    }

    default void initDaoManagers() {
        addDaoManagers();
    }

    default void mappingEntities() {
        addEntities();
    }

    default void initWebServer() {
        addWebContext();
    }

    default void initMessageEndPoint() {
        addMessageEndPoint();
    }
    
    public void addControllers();

    public void addDaoManagers();

    public void addEntities();

    public void addWebContext();
    
    public void addMessageEndPoint();
}
