package com.juanko.core.proxy;

import com.juanko.core.action.query.QueryController;
import com.juanko.core.action.command.CommandController;
import com.juanko.core.dao.PublicDaoManager;
import com.juanko.core.data.nosql.NoSqlDataConnection;

import com.juanko.core.exceptions.ExecutionException;
import java.util.stream.Stream;

/**
 *
 * @author glucero
 */
public interface CoreInterface {

    public <Q extends QueryController> Q getQueryController(Class queryActionClass) throws ExecutionException;

    public <C extends CommandController> Stream<C> getCommandsController(Class commandActionClass) throws ExecutionException;

    public NoSqlDataConnection getNoSqlConnection(String noSqlSourceName) throws ExecutionException;

    public <P extends PublicDaoManager> P getDaoManager(Class daoManager) throws ExecutionException;

}
