package com.juanko.core.action.query;

import com.juanko.core.action.QueryAction;
import com.juanko.core.data.model.RepresentationModel;

import com.juanko.core.exceptions.ControllerException;

/**
 *
 * @author gaston
 */
public abstract class QueryController<Q extends QueryAction,T  extends RepresentationModel> {

    public abstract T execute(Q queryAction) throws ControllerException;
}
