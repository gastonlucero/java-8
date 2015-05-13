package com.juanko.core.exceptions;

import org.apache.log4j.Logger;

/**
 *
 * @author gaston
 */
public class ExecutionException extends RuntimeException {

    private static final Logger logger = Logger.getLogger("seagalCore");

    public ExecutionException() {
    }

    public ExecutionException(String message) {
        super(message);        
        logger.error(message);
    }

    public ExecutionException(String message, Throwable cause) {
        super(message, cause);
        logger.error(message);
    }

}
