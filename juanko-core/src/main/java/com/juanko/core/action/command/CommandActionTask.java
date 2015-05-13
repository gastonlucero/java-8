package com.juanko.core.action.command;

import com.juanko.core.proxy.CoreGateway;
import com.juanko.core.action.CommandAction;
import com.juanko.core.data.model.RepresentationModel;
import com.juanko.core.exceptions.ControllerException;
import com.juanko.core.exceptions.ExecutionException;
import java.util.Map;
import java.util.function.Supplier;

/**
 *
 * @author gaston
 */
public class CommandActionTask<C extends CommandAction, R extends RepresentationModel>
        implements Supplier<R> {

    private C action;
    private boolean transacted;

    public CommandActionTask(C action) {
        this.action = action;
        this.transacted = false;
    }

    public CommandActionTask(C action, boolean transacted) {
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
    public R get() {
        try {
            R result = (R) ((CommandController) CoreGateway.getGateway().getCommandsController(action.getClass()).
                    findFirst().get()).executeAndGet(action);
            return result;
        } catch (ControllerException e) {
            throw new ExecutionException("Error al ejecutar actionTask " + this.getClass().getName(), e);
        }
    }

}
