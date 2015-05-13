package com.juanko.core.data.nosql;

import java.util.Observer;

/**
 *
 * @author glucero
 */
public interface NoSqlSessionFactory extends Observer {

      public void close();
}
