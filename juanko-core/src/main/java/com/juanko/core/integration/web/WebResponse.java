package com.juanko.core.integration.web;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author gaston
 */
public class WebResponse {

    private HttpServletResponse response;

    public WebResponse(HttpServletResponse response) {
        this.response = response;
    }

    public void setContentType(String contentType) {
        response.setContentType(contentType);
    }

    public void addHeader(String header, String value) {
        response.addHeader(header, value);
    }

    public void addCookie(String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("");
        cookie.setMaxAge(-1);
        cookie.setSecure(false);
        response.addCookie(cookie);
    }
}
