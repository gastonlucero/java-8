package com.juanko.core.data.nosql;

/**
 *
 * @author gaston
 */
public abstract class NoSqlSource implements NoSqlSessionFactory {

    public NoSqlSource() {

    }

    public NoSqlSource(boolean withReplica) {

    }

    public abstract NoSqlDataConnection getConnection();
}
