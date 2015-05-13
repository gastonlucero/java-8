package com.juanko.core.integration.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author gaston
 */
public final class WebUtils {

    public static final String GET = "get";
    public static final String POST = "post";
    public static final String PUT = "put";
    public static final String DELETE = "delete";

    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            byte[] buf = new byte[input.available()];
            int size = 0;
            while ((size = input.read(buf)) != -1) {
                output.write(buf, 0, size);
            }
            output.flush();
            return output.toByteArray();
        } finally {
            output.close();
        }
    }
}
