package com.juanko.core.exceptions;

/**
 *
 * @author gaston
 */
public class MessageException extends Exception {

    /**
     * Creates a new instance of <code>WebException</code> without detail
     * message.
     */
    public MessageException() {
    }

    /**
     * Constructs an instance of <code>WebException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public MessageException(String msg) {
        super(msg);
    }

    public MessageException(String message, Throwable cause) {
        super(message, cause);
    }

}
