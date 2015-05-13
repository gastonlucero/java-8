package com.juanko.core.dao;


import com.juanko.core.data.model.RepresentationModel;
import com.juanko.core.data.nosql.NoSqlDataConnection;
import com.juanko.core.proxy.CoreGateway;

/**
 *
 * @author gaston
 */
public abstract class DaoManager<R extends RepresentationModel> {

  

    public NoSqlDataConnection getNoSqlConnection(String noSqlSourceName) {
        return CoreGateway.getGateway().getNoSqlConnection(noSqlSourceName);
    }

}
