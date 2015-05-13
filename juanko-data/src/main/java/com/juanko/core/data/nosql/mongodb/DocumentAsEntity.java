package com.juanko.core.data.nosql.mongodb;



import com.juanko.core.data.model.Entity;
import com.juanko.core.data.nosql.exception.DataAccessException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.bson.Document;

/**
 *
 * @author gaston
 */
public class DocumentAsEntity {

    public static <T extends Entity> T transformToModel(Class clazz, Document dbObject) {
        try {
            T instance = (T) clazz.newInstance();
            dbObject.entrySet().parallelStream().forEach((entry) -> {
                try {
                    Map.Entry<String, Object> fieldKey = (Map.Entry<String, Object>) entry;
                    Optional<FieldWrapper> optional = ReflectionUtils.getFieldColumnName(clazz, fieldKey.getKey());
                    optional.ifPresent((FieldWrapper field) -> {
                        try {
                            Method method = ReflectionUtils.getSetter(clazz, field.getField());
                            if (fieldKey.getValue() instanceof ArrayList) {
                                List<Document> lista = ((List<Document>) fieldKey.getValue());
                                List childList = new ArrayList();
                                lista.stream().forEach((child) -> {
                                    childList.add(DocumentAsEntity.transformToModel((Class) ((ParameterizedType) method.getGenericParameterTypes()[0]).getActualTypeArguments()[0], child));
                                });
                                method.invoke(instance, childList);
                            } else {
                                method.invoke(instance, fieldKey.getValue());
                            }
                        } catch (Exception e) {
                            throw new DataAccessException("Error al mapear la columna" + fieldKey.getKey(), e);
                        }
                    });
                } catch (Exception e) {
                    throw new DataAccessException("", e);
                }
            });
            return instance;
        } catch (Exception e) {
            throw new DataAccessException("Error al mapear dboject en la clase ", e);
        }
    }
}
