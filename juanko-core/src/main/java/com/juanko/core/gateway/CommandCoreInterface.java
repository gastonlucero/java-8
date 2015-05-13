package com.juanko.core.gateway;

import com.juanko.core.action.CommandAction;
import com.juanko.core.data.model.RepresentationModel;
import com.juanko.core.exceptions.ExecutionException;

/**
 *
 * @author gaston
 */
public interface CommandCoreInterface<T extends RepresentationModel> {

    public <C extends CommandAction> void executeCommandAction(final C action, boolean transacted) throws ExecutionException;

    public <C extends CommandAction> T executeAndGetCommandAction(final C action, boolean transacted) throws ExecutionException;

}
