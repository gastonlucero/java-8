package com.juanko.core.data.nosql.mongodb;

import java.lang.reflect.Field;

/**
 *
 * @author gaston
 */
public class FieldWrapper {

    private Field field;
    private Class related;

    public FieldWrapper(Field field, Class related) {
        this.field = field;
        this.related = related;
    }

    public static FieldWrapper get(Field fieldMap, Class related) {
        return new FieldWrapper(fieldMap, related);
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public Class getRelated() {
        return related;
    }

    public void setRelated(Class related) {
        this.related = related;
    }

}
