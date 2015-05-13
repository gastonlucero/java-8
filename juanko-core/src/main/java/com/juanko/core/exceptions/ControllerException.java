package com.juanko.core.exceptions;

import org.apache.log4j.Logger;

/**
 *
 * @author gaston
 */
public class ControllerException extends Exception {

    private static final Logger logger = Logger.getLogger("seagalCore");

    /**
     * Creates a new instance of <code>ControllerException</code> without detail
     * message.
     */
    public ControllerException() {
    }

    /**
     * Constructs an instance of <code>ControllerException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public ControllerException(String msg) {
        super(msg);
        logger.error(msg);
    }

    public ControllerException(String message, Throwable cause) {
        super(message, cause);
        logger.error(message, cause);
    }

}
