package com.juanko.core.action.command;

import com.juanko.core.action.CommandAction;
import com.juanko.core.data.model.RepresentationModel;
import com.juanko.core.exceptions.ControllerException;

/**
 *
 * @author gaston
 */
public abstract class CommandController<C extends CommandAction, T extends RepresentationModel> {

    private boolean transacted;

    public boolean isTransacted() {
        return transacted;
    }

    public void setTransacted(boolean transacted) {
        this.transacted = transacted;
    }

    public abstract void execute(C command) throws ControllerException;

    public abstract T executeAndGet(C command) throws ControllerException;

}
