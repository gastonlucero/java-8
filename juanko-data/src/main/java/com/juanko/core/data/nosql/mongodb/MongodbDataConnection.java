package com.juanko.core.data.nosql.mongodb;

import com.juanko.core.data.model.Entity;
import com.juanko.core.data.nosql.exception.DataAccessException;
import com.juanko.core.data.nosql.NoSqlDataConnection;
import com.juanko.core.data.nosql.QueryData;
import com.juanko.core.data.parser.SeagalJsonParser;
import com.juanko.core.data.parser.RepresentationParserFactory;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.apache.log4j.Logger;
import org.bson.Document;

/**
 *
 * @author gaston
 */
public class MongodbDataConnection extends NoSqlDataConnection {

    private MongoDatabase dataSource;

    private static final Logger logger = Logger.getLogger("seagal");
    private final SeagalJsonParser jsonParser = RepresentationParserFactory.getInstance().getJsonParser();

    public MongodbDataConnection(MongoDatabase dataSource) {
        this.dataSource = dataSource;
    }

    //Falta metodos de busqueda con ordenamineto y limit 
    public <T extends Entity> T find(Class clazz, QueryData query) throws DataAccessException {
        return (T) createSingleQueryFunction(clazz)
                .apply(dataSource.getCollection(ReflectionUtils.getCollectionName(clazz)), query);
    }

    private <T extends Entity> BiFunction<MongoCollection<Document>, QueryData, T> createSingleQueryFunction(Class clazz) {
        BiFunction<MongoCollection<Document>, QueryData, T> findFunction = (collection, query) -> {
            Document response = collection.find(new Document(query)).projection(Projections.excludeId()).first();
            return DocumentAsEntity.transformToModel(clazz, response);
        };
        return findFunction;
    }

    public <T extends Entity> List<T> findList(Class clazz, QueryData query) throws DataAccessException {
        return (List<T>) createListQueryFunction(clazz).apply(dataSource.getCollection(ReflectionUtils.getCollectionName(clazz)), query);
    }

    private <T extends Entity> BiFunction<MongoCollection<Document>, QueryData, List<T>> createListQueryFunction(Class clazz) {
        BiFunction<MongoCollection<Document>, QueryData, List<T>> findFunction = (collection, query) -> {
            List<Document> response = collection.find().filter(new Document(query)).into(new ArrayList<>());
            List<T> result = new ArrayList<>();
            response.parallelStream().forEach(document -> {
                result.add(DocumentAsEntity.transformToModel(clazz, document));
            });
            return result;
        };
        return findFunction;
    }

    public <T extends Entity> void insert(Class clazz, T entity) throws DataAccessException {
        try {
            logger.info("Insertando " + entity.toString());
            MongoCollection collection = dataSource.getCollection(ReflectionUtils.getCollectionName(clazz));
            collection.insertOne(Document.parse(jsonParser.parse(entity)));
        } catch (Exception e) {
            throw new DataAccessException("Error al insertar document", e);
        }
    }

    public <T extends Entity> T insert(Class clazz, Document jsonObject) {
        MongoCollection collection = dataSource.getCollection(ReflectionUtils.getCollectionName(clazz));
        collection.insertOne(jsonObject);
        return (T) DocumentAsEntity.transformToModel(clazz, jsonObject);
    }

    public <T extends Entity> T update(Class clazz, QueryData query) {
        MongoCollection collection = dataSource.getCollection(ReflectionUtils.getCollectionName(clazz));
        Function<MongoCollection, T> replaceFuntion = (coll) -> {
            UpdateResult idResult = collection.updateOne(query.filters(), query.toDocument());
            T replacedData = (T) coll.find(new Document("_id", idResult.getUpsertedId()), clazz).first();
            //(T) DocumentAsEntity.transformToModel(clazz, jsonObject);
            return replacedData;
        };

        return replaceFuntion.apply(collection);
    }

    public <T extends Entity> T replace(Class clazz, QueryData query, Document dataToReplace) {
        MongoCollection collection = dataSource.getCollection(ReflectionUtils.getCollectionName(clazz));
        Function<MongoCollection, T> replaceFuntion = (coll) -> {
            UpdateResult idResult = coll.replaceOne(query.filters(), dataToReplace);
            T replacedData = (T) coll.find(new Document("_id", idResult.getUpsertedId()), clazz).first();
            return replacedData;
        };
        return replaceFuntion.apply(collection);
    }

    public long delete(Class clazz, QueryData query) {
        MongoCollection collection = dataSource.getCollection(ReflectionUtils.getCollectionName(clazz));
        DeleteResult result = collection.deleteMany(query.filters());
        return result.getDeletedCount();
    }

    public Object singleResult(String collectionName, QueryData queryData) {
        MongoCollection collection = dataSource.getCollection(collectionName);
        Object result = collection.find(queryData.filters())
                .filter(queryData.toDocument())
                .projection(queryData.projections())
                .sort(queryData.sorts())
                .first();
        return result;
    }

    public Function<String, MongoCollection> plainQuery() {
        Function<String, MongoCollection> collection = (collName) -> {
            return dataSource.getCollection(String.valueOf(collName));
        };
        return collection;
    }

}
