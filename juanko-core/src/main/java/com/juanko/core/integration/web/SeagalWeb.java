package com.juanko.core.integration.web;

import com.juanko.core.exceptions.WebException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

/**
 *
 * @author gaston
 */
public class SeagalWeb {

    private static List<ContextHandler> contexts = new ArrayList<>();
    private static Server server;

    public static void main(String[] args) throws Exception {

        new SeagalWeb().initMethod(9000, 10);
//        new SeagalWeb().a();
    }

    public static void addGet(String path, BiFunction<WebRequest, WebResponse, Object> handle,
            Function<Object, String> transfomer) {
        ContextHandler context = new ContextHandler();
        context.setContextPath(path);
        context.setHandler(
                new AbstractHandler() {
                    @Override
                    public void handle(String string, Request request,
                            HttpServletRequest httpReq, HttpServletResponse httpRes) throws IOException, ServletException {
                        try {
                            request.getMethod();
                            Object result = handle.apply(new WebRequest(request), new WebResponse(httpRes));
                            httpRes.setStatus(HttpServletResponse.SC_OK);
                            if (transfomer != null) {
                                httpRes.getOutputStream().write(transfomer.apply(result).getBytes());
                            } else {
                                httpRes.getOutputStream().write(result.toString().getBytes());
                            }
                        } catch (Exception e) {
                            httpRes.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        } finally {
                            httpRes.getOutputStream().flush();
                        }
                    }
                });
        contexts.add(context);
    }

    public void initMethod(int port, int pool) {
        try {
            QueuedThreadPool threadPool = new QueuedThreadPool(15);
            threadPool.setMaxThreads(15);
            threadPool.setMinThreads(10);
            threadPool.setDetailedDump(false);
            ServerConnector connector = new ServerConnector(new Server(threadPool));
            connector.setIdleTimeout(TimeUnit.HOURS.toMillis(1));
            connector.setSoLingerTime(-1);
            connector.setPort(port);
            connector.setAcceptQueueSize(100);
            Server server = connector.getServer();
            server.setConnectors(new Connector[]{connector});
            HandlerList handlers = new HandlerList();
            SeagalWeb.addGet("/odometro", (request, response) -> {
                try {
                    return request.getBody();
                } catch (Exception e) {
                    throw new WebException(e.getMessage(), e);
                }
            }, null);
            contexts.parallelStream().forEach((endpoint) -> {
                handlers.addHandler(endpoint);
            });
            server.setHandler(handlers);
            server.setDumpAfterStart(false);
            server.setDumpBeforeStop(false);
            server.setStopAtShutdown(true);
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void a() {
        try {
            ExecutorThreadPool threadPool = new ExecutorThreadPool(Executors.newFixedThreadPool(10));
            ServerConnector connector = new ServerConnector(new Server(threadPool));
            // Set some timeout options to make debugging easier.
            connector.setIdleTimeout(TimeUnit.HOURS.toMillis(1));
            connector.setSoLingerTime(-1);
            connector.setPort(9000);
            Server server = connector.getServer();
            server.setConnectors(new Connector[]{connector});
            HandlerList handlers = new HandlerList();
            SeagalWeb.addGet("/odometro", (request, response) -> {
                try {
                    return request.getBody();
                } catch (Exception e) {
                    throw new WebException(e.getMessage(), e);
                }
            }, (result) -> {
                return result.toString();
            });
            contexts.parallelStream().forEach((endpoint) -> {
                handlers.addHandler(endpoint);
            });
            server.setHandler(handlers);
            server.start();
            server.join();
        } catch (Exception e) {

        }
    }
}
