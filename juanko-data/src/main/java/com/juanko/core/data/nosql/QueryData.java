package com.juanko.core.data.nosql;

import java.util.HashMap;
import org.bson.Document;
import org.bson.conversions.Bson;

/**
 *
 * @author gaston
 */
public class QueryData extends HashMap<String, Object> {

    private Bson filters = new Document();
    private Bson projections = new Document();
    private Bson sorts = new Document();

    public QueryData(String key, Object value) {
        super();
        this.put(key, value);
    }

    public QueryData addValue(String key, Object value) {
        this.put(key, value);
        return this;
    }

    public Document toDocument() {
        return new Document(this);
    }

    public void setFilters(Bson filters) {
        this.filters = filters;
    }

    public Bson filters() {
        return this.filters;
    }

    public void setProjections(Bson projections) {
        this.projections = projections;

    }

    public Bson projections() {
        return this.projections;
    }

    public void setSorts(Bson sorts) {
        this.sorts = sorts;

    }

    public Bson sorts() {
        return this.sorts;
    }
}
