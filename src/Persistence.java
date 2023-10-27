import com.mongodb.MongoCredential;
import com.mongodb.client.*;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.*;

import static com.mongodb.client.model.Filters.eq;

public class Persistence {
    private MongoDatabase database;
    public Persistence(){
        database = DBManager.getConnection().getDatabase("MessageApp");
    }

    public Collection<String> getMessageQueue(String user){
        Bson projectionFields = Projections.fields(
                Projections.include("messageQueue"),
                Projections.excludeId());

        Document document = database.getCollection("MessageQueue")
                .find(eq("user", user))
                .projection(projectionFields)
                .first();

        Collection<String> messageQueue = null;
        if(document != null){
            messageQueue = (Collection<String>) document.get("messageQueue");
            if(messageQueue != null) {
                UpdateResult result = database.getCollection("MessageQueue")
                        .updateOne(document, Updates.unset("messageQueue"));
            }
        }

        return messageQueue;
    }

    public void addToMessageQueue(String user, Collection<String> messageQueue){
        Document document = database.getCollection("MessageQueue")
                .find(eq("user", user))
                .first();

        if(document == null){
            database.getCollection("MessageQueue")
                    .insertOne(new Document("user", user).append("messageQueue", messageQueue));
        }
        else if(!document.containsKey("messageQueue")) {
            UpdateResult result = database.getCollection("MessageQueue")
                    .updateOne(document, Updates.set("messageQueue", messageQueue));
        }
        else {
            UpdateResult result = database.getCollection("MessageQueue")
                    .updateOne(document, Updates.addEachToSet("messageQueue", (List<String>) messageQueue));
        }
    }
}
