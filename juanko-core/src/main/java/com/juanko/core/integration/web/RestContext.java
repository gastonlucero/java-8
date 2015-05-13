package com.juanko.core.integration.web;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.juanko.core.annotations.RestEndPoint;
import com.juanko.core.annotations.WebPath;

import com.juanko.core.exceptions.WebException;

import com.juanko.core.data.model.RepresentationModel;
import com.juanko.core.data.parser.RepresentationParserFactory;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import spark.Request;
import spark.Spark;

/**
 *
 * @author gaston
 */
public abstract class RestContext {

    private String path;
    private ObjectMapper mapper;
    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    public RestContext() {
        this.path = this.getClass().getAnnotation(WebPath.class).path();
        this.mapper = new ObjectMapper();
        mapper.setDateFormat(format);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        startEndPoints();
    }

    private void startEndPoints() {
        try {
            Arrays.asList(this.getClass().getDeclaredMethods()).stream().forEachOrdered((Method method) -> {
                RestEndPoint endPoint = method.getAnnotation(RestEndPoint.class);
                switch (endPoint.type()) {
                    case WebUtils.GET: {
                        Spark.get(this.path + endPoint.path(), (request, response) -> {
                            
                            Object result = method.invoke(this, parseRequestParameters(method, request).toArray());
                            return result;
                        }, (result) -> {                            
                            return RepresentationParserFactory.getInstance().getJsonParser().parse(result);
                        });
                        break;
                    }
                    case WebUtils.POST: {
                        Spark.post(this.path + endPoint.path(), (request, response) -> {
                            Object result = method.invoke(this, mapper.readValue(request.bodyAsBytes(), method.getParameters()[0].getType()));
                            return result;
                        }, (result) -> {
                            return mapper.writeValueAsString(result);
                        });
                        break;
                    }
                    case WebUtils.PUT: {
                        Spark.put(this.path + endPoint.path(), (request, response) -> {
                            Object result = method.invoke(this, mapper.readValue(request.bodyAsBytes(), method.getParameters()[0].getType()));
                            return result;
                        }, (result) -> {
                            return mapper.writeValueAsString(result);
                        });
                        break;
                    }
                    case WebUtils.DELETE: {
                        Spark.delete(this.path + endPoint.path(), (request, response) -> {
                            Object result = method.invoke(this, mapper.readValue(request.bodyAsBytes(), method.getParameters()[0].getType()));
                            return result;
                        }, (result) -> {
                            return mapper.writeValueAsString(result);
                        });
                        break;
                    }
                }

            }
            );
        } catch (Exception e) {
            throw new WebException(e.getMessage());
        }
    }

    private List<Object> parseRequestParameters(Method method, Request request) {
        List<Object> invokeList = new ArrayList<>();
        Arrays.asList(method.getParameters()).stream().forEach((param) -> {
            try {
                Type type = param.getParameterizedType();
                Class clazz = null;
                Class listType = null;
                if (type instanceof Class) {
                    clazz = (Class) type;
                } else if (type instanceof ParameterizedType) {
                    clazz = (Class) ((ParameterizedType) type).getRawType();
                    listType = (Class) ((ParameterizedType) type).getActualTypeArguments()[0];
                }
                if (clazz.isAssignableFrom(List.class)) {
                    List list = new ArrayList();
                    if (request.queryParams(param.getName()).contains(",")) {
                        String splitList[] = request.queryParams(param.getName()).split(",");
                        for (String listParam : splitList) {
                            list.add(listType.getDeclaredMethod("valueOf", String.class).invoke(new Object(), listParam));
                        }
                    } else {
                        list.add(listType.getDeclaredMethod("valueOf", String.class).invoke(new Object(), request.queryParams(param.getName())));
                    }
                    invokeList.add(list);
                } else if (RepresentationModel.class.isAssignableFrom(clazz)) {
                    invokeList.add(mapper.readValue(request.queryParams(param.getName()), clazz));
                } else if (String.class.isAssignableFrom(clazz)) {
                    invokeList.add(request.queryParams(param.getName()));
                } else if (Date.class.isAssignableFrom(clazz)) {
                    invokeList.add(format.parse(request.queryParams(param.getName())));
                } else {
                    String a = request.queryParams(param.getName());
                    a.toString();
                    invokeList.add(clazz.getDeclaredMethod("valueOf", String.class).invoke(new Object(),
                            request.queryParams(param.getName())));
                }
            } catch (Exception e) {
                throw new WebException(e.getMessage(), e);
            }
        });
        return invokeList;
    }

}
