package com.juanko.core.integration.message;

import com.juanko.core.data.model.RepresentationModel;



/**
 *
 * @author gaston
 */
@FunctionalInterface
public interface SenderMessage<E extends RepresentationModel> {

    void sendMessage(E message);
}
