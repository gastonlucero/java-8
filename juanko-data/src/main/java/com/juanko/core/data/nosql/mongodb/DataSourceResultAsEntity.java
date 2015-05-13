package com.juanko.core.data.nosql.mongodb;

import com.juanko.core.data.model.Entity;
import com.juanko.core.data.nosql.exception.DataAccessException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author gaston
 */
public class DataSourceResultAsEntity {

//    public static <T extends Entity> T transform(Class clazz, ResultSet rs) {
//        try {
//            ReflectionUtils.addEntity(clazz);
//            Map<String, Map<String, Object>> columnNames = new HashMap<>();
//            ResultSetMetaData metaData = rs.getMetaData();
//            for (int i = 0; i < metaData.getColumnCount(); i++) {
//                if (metaData.getTableName(i + 1).trim().equals("")) {
//                    columnNames.put(metaData.getTableName(1), new HashMap<>());
//                } else {
//                    columnNames.put(metaData.getTableName(i + 1), new HashMap<>());
//                }
//            }
//            for (int i = 0; i < metaData.getColumnCount(); i++) {
//                String a = metaData.getTableName(i + 1).trim();
//                columnNames.get(a.equals("") ? metaData.getTableName(1) : a).put(metaData.getColumnLabel(i + 1), rs.getObject(i + 1));
//            }
//            Map<Class, T> results = new HashMap<>();
//            columnNames.entrySet().stream().forEach((table) -> {
//                try {
//                    Map.Entry<String, Map<String, Object>> dbCol = (Map.Entry<String, Map<String, Object>>) table;
//                    Class tableClazz = ReflectionUtils.getMappingClass(dbCol.getKey());//nombre de la tabla
//                    T instance = (T) tableClazz.newInstance();
//                    dbCol.getValue().entrySet().parallelStream().forEach((entry) -> { //cada columna del query
//                        try {
//                            Map.Entry<String, Object> fieldKey = (Map.Entry<String, Object>) entry; //nombre columna-Valor
//                            Optional<Field> optional = ReflectionUtils.getFieldColumnName(tableClazz, fieldKey.getKey());
//                            optional.ifPresent((Field field) -> {
//                                try {
//                                    Method method = tableClazz.getDeclaredMethod("set" + field.getName().substring(0, 1).toUpperCase()
//                                            + field.getName().substring(1), field.getType());
//                                    method.invoke(instance, fieldKey.getValue());
//                                } catch (Exception e) {
//                                    throw new DataAccessException("Error al mapear la columna" + field.getName(), e);
//                                }
//                            });
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    });
//                    results.put(tableClazz, instance);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            });
//            T objectResult = (T) results.get(clazz);
//            results.remove(clazz);
//            results.entrySet().parallelStream().forEach((c) -> {
//                try {
//                    Method method = clazz.getDeclaredMethod("set" + c.getKey().getSimpleName(), c.getKey());
//                    method.invoke(objectResult, c.getValue());
//                } catch (Exception e) {
//                    throw new DataAccessException("Error al mapear la columna", e);
//                }
//            });
//            return (T) objectResult;
//        } catch (Exception e) {
//            throw new DataAccessException("Error al completar el resultado ", e);
//        }
//    }
    public static <T extends Entity> T transformToModel(Class clazz, ResultSet rs) {
        try {
            ResultSetMetaData metaData = rs.getMetaData();
            Map<String, Object> columnNames = new HashMap<>();
            for (int i = 0; i < metaData.getColumnCount(); i++) {
                columnNames.put(metaData.getColumnLabel(i + 1), rs.getObject(i + 1));
            }
            T instance = (T) clazz.newInstance();
            columnNames.entrySet().parallelStream().forEach((entry) -> {
                Map.Entry<String, Object> dbCol = (Map.Entry<String, Object>) entry;
                Optional<FieldWrapper> optional = ReflectionUtils.getFieldColumnName(clazz, dbCol.getKey());
                optional.ifPresent((FieldWrapper field) -> {
                    try {
                        Method method = ReflectionUtils.getSetter(clazz, field.getField());
                        if (field.getRelated().equals(Void.class)) {
                            method.invoke(instance, dbCol.getValue());
                        } else {
                            try {
                                PreparedStatement ps = rs.getStatement().getConnection().prepareStatement("Select * from " + ReflectionUtils.getMappingName(field.getRelated()).get() + " where id = ? ");
                                ps.setObject(1, dbCol.getValue());
                                ResultSet newResultSet = ps.executeQuery();
                                if (List.class.isAssignableFrom(field.getField().getType())) {
                                    List<T> relatedList = new ArrayList<>();
                                    while (newResultSet.next()) {
                                        relatedList.add(DataSourceResultAsEntity.transformToModel(field.getRelated(), newResultSet));
                                    }
                                    method.invoke(instance, relatedList);
                                } else {
                                    newResultSet.next();
                                    method.invoke(instance, DataSourceResultAsEntity.transformToModel(field.getRelated(), newResultSet));
                                }
                            } catch (Exception e) {
                                throw new DataAccessException("Error al mapear la columna" + dbCol.getKey(), e);
                            }
                        }
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        throw new DataAccessException("Error al mapear la columna" + dbCol.getKey(), e);
                    }
                });
            });
            return instance;
        } catch (IllegalAccessException | InstantiationException | SQLException e) {
            throw new DataAccessException("Error al completar el resultado ", e);
        }
    }
}
