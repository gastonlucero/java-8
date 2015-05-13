package com.juanko.core.exceptions;

/**
 *
 * @author gaston
 */
public class WebException extends RuntimeException {

    /**
     * Creates a new instance of <code>WebException</code> without detail
     * message.
     */
    public WebException() {
    }

    /**
     * Constructs an instance of <code>WebException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public WebException(String msg) {
        super(msg);
    }

    public WebException(String message, Throwable cause) {
        super(message, cause);
    }

}
