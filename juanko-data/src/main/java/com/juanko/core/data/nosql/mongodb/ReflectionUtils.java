package com.juanko.core.data.nosql.mongodb;


import com.juanko.core.data.annotation.FieldColumn;
import com.juanko.core.data.annotation.TableOrCollection;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author gaston
 */
public final class ReflectionUtils  {

    
    private static final Map<Class, Map<String, FieldWrapper>> entitiesMapping = Collections.synchronizedMap(new HashMap<>());
    private static final Map<Class, String> persistenceMapping = Collections.synchronizedMap(new HashMap<>());
    private static final Map<Class, Map<Field, Method>> entitiesMethods = Collections.synchronizedMap(new HashMap<>());

  
    public static void addEntity(Class entity) {
        try {
            if (!entitiesMapping.containsKey(entity)) {
                Map<String, FieldWrapper> fieldMap = new HashMap<>();
                Map<Field, Method> methodMap = new HashMap<>();
                for (Field field : entity.getDeclaredFields()) {
                    if (field.isAnnotationPresent(FieldColumn.class)) {
                        fieldMap.putIfAbsent(field.getAnnotation(FieldColumn.class).name(), 
                              FieldWrapper.get(field, field.getAnnotation(FieldColumn.class).relatedClass()));
                        methodMap.putIfAbsent(field, entity.getDeclaredMethod("set" + field.getName().substring(0, 1).toUpperCase()
                                + field.getName().substring(1), field.getType()));
                    }
                }
                entitiesMapping.putIfAbsent(entity, fieldMap);
                entitiesMethods.putIfAbsent(entity, methodMap);
                if (entity.isAnnotationPresent(TableOrCollection.class)) {
                    persistenceMapping.putIfAbsent(entity, ((TableOrCollection) entity.getAnnotation(TableOrCollection.class)).name());
                }
            }
        } catch (NoSuchMethodException e) {
e.printStackTrace();
        }
    }

    public static String getCollectionName(Class clazz) {
        if (clazz.isAnnotationPresent(TableOrCollection.class)) {
            return ((TableOrCollection) clazz.getAnnotation(TableOrCollection.class)).name();
        } else {
            throw new RuntimeException();
        }
    }

    public static Optional<FieldWrapper> getFieldColumnName(Class clazz, String columnName) {
        return Optional.ofNullable(entitiesMapping.get(clazz).get(columnName));
    }

    public static Optional<String> getMappingName(Class clazz) {
        return Optional.of(persistenceMapping.get(clazz));
    }

    public static Class getMappingClass(String name) {
        Class result = persistenceMapping.entrySet().stream().filter((entry) -> {
            return entry.getValue().equals(name);
        }).findFirst().get().getKey();
        return result;
    }

    public static Class getObjectField(Class clazz, String name) {
        return Arrays.asList(clazz.getDeclaredFields()).stream().filter((f) -> {
            return f.getType().isAnnotationPresent(TableOrCollection.class)
                    && ((TableOrCollection) f.getType().getAnnotation(TableOrCollection.class)).name().equals(name);
        }).findFirst().get().getType();
    }

    public static Method getSetter(Class clazz, Field fieldName) {
        return entitiesMethods.get(clazz).get(fieldName);
    }
}
