package com.juanko.core.action.command;

import com.juanko.core.proxy.CoreGateway;
import com.juanko.core.action.CommandAction;
import com.juanko.core.data.model.RepresentationModel;
import com.juanko.core.exceptions.ControllerException;
import com.juanko.core.exceptions.ExecutionException;

/**
 *
 * @author gaston
 */
public class CommandAsyncActionTask<C extends CommandAction, T extends RepresentationModel>
        implements Runnable {

    private C action;
    private boolean transacted;

    public CommandAsyncActionTask(C action) {
        this.action = action;
        this.transacted = false;
    }

    public CommandAsyncActionTask(C action, boolean transacted) {
        this.action = action;
        this.transacted = transacted;
    }

    public C getCommand() {
        return action;
    }

    public void setCommand(C command) {
        this.action = command;
    }

    @Override
    public void run() {
        CoreGateway.getGateway().getCommandsController(action.getClass()).forEach((controller) -> {
            try {
                ((CommandController) controller).execute(action);
            } catch (ControllerException e) {
                throw new ExecutionException("Error al ejecutar actionTask " + this.getClass().getName(), e);
            }
        });
    }

}
