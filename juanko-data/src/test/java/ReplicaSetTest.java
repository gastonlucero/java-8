
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;

import java.net.UnknownHostException;
import static java.util.Arrays.*;
import org.bson.Document;

public class ReplicaSetTest {

    public static void main(String[] args) throws UnknownHostException, InterruptedException {
        MongoClient client = new MongoClient(asList(
                new ServerAddress("localhost", 27017),
                new ServerAddress("localhost", 27018),
                new ServerAddress("localhost", 27019)));

        MongoCollection test = client.getDatabase("course").getCollection("replica.test");
        test.drop();

        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            test.insertOne(new Document("_id", i));
            System.out.println("Inserted document: " + i);
            Thread.sleep(500);
        }
    }
}
