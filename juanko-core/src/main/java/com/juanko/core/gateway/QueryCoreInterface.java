package com.juanko.core.gateway;

import com.juanko.core.action.QueryAction;
import com.juanko.core.data.model.RepresentationModel;

import com.juanko.core.exceptions.ExecutionException;

/**
 *
 * @author gaston
 */
public interface QueryCoreInterface<T extends RepresentationModel> {

    public <Q extends QueryAction> T executeQueryAction(final Q action) throws ExecutionException;
}
