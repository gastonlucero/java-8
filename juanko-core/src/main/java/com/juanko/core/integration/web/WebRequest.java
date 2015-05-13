package com.juanko.core.integration.web;

import org.eclipse.jetty.server.Request;

/**
 *
 * @author gaston
 */
public class WebRequest {

    private Request request;
    private String body;
    private byte[] bodyBytes;

    public WebRequest(Request request) {
        this.request = request;
        readBody();
    }

    public String queryParams(String queryParam) {
        return request.getParameter(queryParam);
    }

    public String queryString() {
        return request.getQueryString();
    }

    public String getBody() {
        return body;
    }

    public byte[] getBodyBytes() {
        return bodyBytes;
    }

    private void readBody() {
        try {
            bodyBytes = WebUtils.toByteArray(request.getInputStream());
            body = new String(bodyBytes);
        } catch (Exception e) {
        }
    }

    public int contentLength() {
        return request.getContentLength();
    }

    public String contentType() {
        return request.getContentType();
    }

}
