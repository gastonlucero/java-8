package com.juanko.core.listener.event;

/**
 *
 * @author gaston
 */
public interface CoreEventsListener {

    public <E extends SeagalEvent> void processEvents();
}
