package com.juanko.core.data.nosql.exception;

import org.apache.log4j.Logger;

/**
 *
 * @author gaston
 */
public class DataAccessException extends RuntimeException {

    public DataAccessException(String message, Exception e) {
        super(message, e);
        Logger.getLogger("coreData").error(message, e);
    }

    public DataAccessException() {
        super();
    }

}
