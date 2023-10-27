import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class DBManager {
    private static DBManager instance = null;
    private MongoClient connection;

    private static DBManager getInstance(){
        if(instance == null){
            instance = new DBManager();
        }
        return instance;
    }

    private DBManager(){
        String uri = "mongodb://localhost:27017/test?authSource=admin";

        try{
            connection = MongoClients.create(uri);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static MongoClient getConnection(){
        return getInstance().connection;
    }
}
