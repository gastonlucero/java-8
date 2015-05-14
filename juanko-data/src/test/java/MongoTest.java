
import com.juanko.core.data.nosql.QueryData;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import static com.mongodb.client.model.Filters.eq;
import com.mongodb.client.model.UpdateOptions;
import java.util.Date;
import java.util.Properties;
import java.util.function.Function;
import org.apache.log4j.Logger;
import org.bson.Document;

/**
 *
 * @author gaston
 */
public class MongoTest {

    private static final Logger logger = Logger.getLogger("org.mongodb.driver");

   static  class a implements Runnable{

        @Override
        public void run() {
            System.out.println("hilo");
        }
        
    }
    public static void main(String[] args) throws Throwable {
        try {
           
        Date a = new Date(1428676953000L);
            a.toString();
            Properties p = new Properties();
            p.put("database", "seagal");
            p.put("host", "localhost");      
            MongoClient mongo = new MongoClient("localhost");
            MongoDatabase db = mongo.getDatabase("seagal");
 
        
//            ReflectionNoSqlUtils.addEntity(Students.class);
//            ReflectionNoSqlUtils.addEntity(Score.class);
            Function<String, MongoCollection> collecion = (collName) -> {
                return db.getCollection(collName);
            };
            Plan plan =new Plan();
            plan.setKey("Hola");
            
MongoCollection odoPerHour= db.getCollection("odometer_per_hour");
//odoPerHour.insertOne(new Document("modemId",plan.getKey()).append("tramas", new ArrayList<>()));
        odoPerHour.updateOne(eq("modemId", plan.getKey()),
                                  new Document("$push", new Document("tramas", new Document("www","eeee").append("aasddasda","sda"))),new UpdateOptions().upsert(true));
        
            MongoCollection coll = db.getCollection("polygono");
            coll.insertOne(new Document("$geometry",new Document("punto",new double[] { -73.988135, 40.741404 })));
            QueryData query = new QueryData("dispositivoId", 3).addValue("eventoId", new Document("$eq", 141));
            query.setFilters(Filters.gt("eventoId", 100));
            query.toDocument();
            Block<Document> printBlock = new Block<Document>() {
                @Override
                public void apply(final Document document) {
                    System.out.println(document.toJson());
                }
            };
            System.setProperty("DEBUG.MONGO", "true");
//            coll.find(new Document("eventoId", -3), User.class).filter(Projections.excludeId()).first();
//            System.out.print(coll.deleteMany(Filters.eq("eventoId", 141)).getDeletedCount());

//            Students ss =mds.getConnection().find(Students.class,new QueryData().addValue("_id", 137));
//            ss.toString();
//            ExecutorService exec = Executors.newFixedThreadPool(5);
//            
//            CompletableFuture completable = CompletableFuture.supplyAsync(() -> {
//                System.out.println(Thread.currentThread().getId());
//                return 10;
//            }, exec).thenApplyAsync((v) -> {
//                System.out.println(Thread.currentThread().getId());
//                return v + 5;
//            },exec).thenCompose((t)->CompletableFuture.supplyAsync(()->{
//                System.out.println(Thread.currentThread().getId());
//                return t+1;
//            }));
//            
//            Object o= completable.join();
//            o.toString();
//            User u = new User();
//            
//            Field f=u.getClass().getDeclaredFields()[0];
//            
//            MethodHandle mh = MethodHandles.lookup().findSetter(User.class, "company",String.class);
//            mh.invoke(u,"sasa");
//            MethodHandle mh = lookup.findVirtual(User.class, "hello1",);
//            
//            mh.java 8Exact(u, "ssss");
//            Properties p = new Properties();
//            p.put("user", "root");
//            p.put("password", "root");
//            p.put("size", 1);
//            MysqlDataSource myDs = new MysqlDataSource(p);
//            MysqlDataConnection conn = myDs.getConnection();
//            User u = conn.select("select * from usuario limit 1", (ps) -> {
//                try {
//                    ResultSet rs = ps.executeQuery();
//                    rs.next();
//                    return DataSourceResultAsEntity.transform(User.class, rs);
////                    return new User(rs.getLong("id"), rs.getString("username"), rs.getString("rut"));
//                } catch (Exception e) {
//                    throw new RuntimeException();
//                }
//            });
//            u.toString();
//            ;
//            ByteGetter.BinaryByteGetter bytes = new ByteGetter.BinaryByteGetter(new BigInteger("0101000020E610000028680CADB0AA51C08539393389BF40C0", 16).toByteArray());
//            ValueGetter valueGet=null ;
//            if (bytes.get(0) == ValueGetter.XDR.NUMBER) { // XDR
//            valueGet=new  ValueGetter.XDR(bytes);
//        } else if (bytes.get(0) == ValueGetter.NDR.NUMBER) {
//            valueGet=new ValueGetter.NDR(bytes);
//        }            
//            byte endian = valueGet.getByte(); // skip and test endian flag
//            if (endian != valueGet.endian) {
//                throw new IllegalArgumentException("Endian inconsistency!");
//            }
//            int typeword = valueGet.getInt();
//            int srid=0;
//            boolean inheritSrid=false;
//            int realtype = typeword & 0x1FFFFFFF; // cut off high flag bits
//
//            boolean haveZ = (typeword & 0x80000000) != 0;
//            boolean haveM = (typeword & 0x40000000) != 0;
//            boolean haveS = (typeword & 0x20000000) != 0;
//    if (haveS) {
//            int newsrid = org.postgis.Geometry.parseSRID(valueGet.getInt());
//            if (inheritSrid && newsrid != srid) {
//                throw new IllegalArgumentException("Inconsistent srids in complex geometry: " + srid + ", " + newsrid);
//            } else {
//                srid = newsrid;
//            }
//        } else if (!inheritSrid) {
//            srid = org.postgis.Geometry.UNKNOWN_SRID;
//        }
//            Geometry result;
//            switch (realtype) {
//                case org.postgis.Geometry.POINT: {
//                    double X = valueGet.getDouble();
//                    double Y = valueGet.getDouble();
//                    new Point(X, Y);
//                    break;
//                }}
//            Properties p = new Properties();
//            p.put("user", "postgres");
//            p.put("password", "postgres");
//            p.put("size", 5);
//            RefReflectionUtilsdEntity(Comuna.class);
//            SqlDataSource myDs = new SqlDataSource(p);
//            System.out.println(System.currentTimeMillis());
//
//            myDs.getConnection().selectIterableList("select * from geocerca_circular limit 20", Comuna.class).parallel().forEach((c) -> {
//                System.out.println(c.toString());
//            }
//            );
//            List<Comuna> lista = myDs.getConnection().selectList("select * from geocerca_circular limit 10", (ps) -> {
//                try {
//                    ResultSet rs = ps.executeQuery();
//                    List<Comuna> esultado = new ArrayList<>();
//                    while (rs.next()) {
//                        esultado.add(DataSourceResultAsEntity.transform(Comuna.class, rs));
//                    }
//                    myDs.newIdleConnection();
//                    return esultado;
//                } catch (Exception e) {
//                    throw new RuntimeException();
//                }
//            });
//            Comuna c = myDs.getConnection().select("select * from geocerca_circular limit 1", (ps) -> {
//                try {
//                    ResultSet rs = ps.executeQuery();
//                    rs.next();
//                    Comuna comuna = DataSourceResultAsEntity.transform(Comuna.class, rs);
//                    myDs.newIdleConnection();
//                    return comuna;
//                } catch (Exception e) {
//                    throw new RuntimeException();
//                }
//            });
//            System.out.println(System.currentTimeMillis());
//            c.toString();
        } catch (Throwable e) {
             e.printStackTrace();
        }
    }

}
