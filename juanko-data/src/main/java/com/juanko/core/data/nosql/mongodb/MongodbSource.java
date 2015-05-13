package com.juanko.core.data.nosql.mongodb;

import com.juanko.core.data.utils.ResourcesManager;
import com.juanko.core.data.nosql.exception.DataAccessException;
import com.juanko.core.data.nosql.NoSqlSource;
import com.juanko.core.data.nosql.Replica;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import java.util.List;
import java.util.Observable;
import java.util.Properties;

/**
 *
 * @author gaston
 */
public class MongodbSource extends NoSqlSource {

    private MongoClient mongo;
    private MongoDatabase dataSource;

    private static final String MONGO_HOST = "mongo.host";
    private static final String MONGO_DB = "mongo.database";

    public MongodbSource() {
        try {
            this.mongo = new MongoClient(ResourcesManager.getPropertyValue(MONGO_HOST));
            this.dataSource = mongo.getDatabase(ResourcesManager.getPropertyValue(MONGO_DB));
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    public MongodbSource(List<Replica> replicas) {
        super();
    }

    @Override
    public MongodbDataConnection getConnection() {
        return new MongodbDataConnection(dataSource);
    }

    @Override
    public void update(Observable o, Object arg) {
        //Si cambio algo
    }

    @Override
    public void close(){
        this.mongo.close();
    }
}
