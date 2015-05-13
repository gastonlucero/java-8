package com.juanko.core.action.query;

import com.juanko.core.proxy.CoreGateway;
import com.juanko.core.action.QueryAction;
import com.juanko.core.data.model.RepresentationModel;

import com.juanko.core.exceptions.ControllerException;
import com.juanko.core.exceptions.ExecutionException;

import java.util.function.Supplier;

/**
 *
 * @author gaston
 */
public class QueryActionTask<Q extends QueryAction, R extends RepresentationModel>
        implements Supplier<R> {

    private Q action;

    public QueryActionTask(Q action) {
        this.action = action;
    }

    public Q getCommand() {
        return action;
    }

    public void setCommand(Q action) {
        this.action = action;
    }

    @Override
    public R get() {
        try {        
            return (R) CoreGateway.getGateway().getQueryController(action.getClass()).execute(action);
        } catch (ControllerException e) {
            throw new ExecutionException("Erro al ejecutar queryTask " + this.getClass().getName(), e);
        }
    }

}
