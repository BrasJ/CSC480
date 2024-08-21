package csc480;

import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.conversions.Bson;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;

public class connection {
    public static void main(String[] args) {
        // Replace the placeholder with your Atlas csc480.csc480.connection string
        String uri = "mongodb://127.0.0.1:27017";

        // Construct a ServerApi instance using the ServerApi.builder() method
        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(uri))
                .serverApi(serverApi)
                .build();

        // Create a new client and connect to the server
        try (MongoClient mongoClient = MongoClients.create(settings)) {
            MongoDatabase database = mongoClient.getDatabase("TroopManagementApp");
            MongoCollection<Document> scoutCollection = database.getCollection("Scout");

            //createScout(scoutCollection);
            //deleteScout(scoutCollection);
            //updateEmail(scoutCollection);
            //updatePosition(scoutCollection);
            //updateRank(scoutCollection);
            //updateMeritBadge(scoutCollection);

            // Retrieve a specific field from a document as text
            Bson getRankRequirements = eq("rank", "Star");
            for (Document docRank : database.getCollection("Rank").find(getRankRequirements)) {
                String jsonRank = docRank.toJson();
                ObjectMapper mapperRank = new ObjectMapper();
                JsonNode nodeRank = null;
                try {
                    nodeRank = mapperRank.readTree(jsonRank);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(wrap(nodeRank.get("rank").asText()));
                System.out.println(wrap(nodeRank.get("reqs").asText()));
            }

            // Select a specific scout (document) from within the Scout collection and retrieve the requirements for a
            // specific merit badge in that scout's document as text
            Bson getMeritBadge = and(eq("firstName", "John"), eq("lastName", "Doe"));
            for (Document docBadge : scoutCollection.find(getMeritBadge)) {
                String jsonBadge = docBadge.toJson();
                ObjectMapper mapperBadge = new ObjectMapper();
                JsonNode nodeBadge = null;
                try {
                    nodeBadge = mapperBadge.readTree(jsonBadge);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                Bson getRequirements = eq("Name", nodeBadge.get("MeritBadge").asText());
                for (Document docReqs : database.getCollection("MeritBadge").find(getRequirements)) {
                    String jsonReqs = docReqs.toJson();
                    ObjectMapper mapperReqs = new ObjectMapper();
                    JsonNode nodeReqs = null;
                    try {
                        nodeReqs = mapperReqs.readTree(jsonReqs);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println(wrap(nodeReqs.get("Name").asText()));
                    System.out.println(wrap(nodeReqs.get("Requirements").asText()));
                }
            }

            try {
                // Send a ping to confirm a successful connection
                Bson command = new BsonDocument("ping", new BsonInt64(1));
                Document commandResult = database.runCommand(command);
                System.out.println("Pinged your deployment. You successfully connected to MongoDB!");
            } catch (MongoException me) {
                System.err.println(me);
            }
        }
    }

    private static void createScout(MongoCollection<Document> scoutCollection) {
        String fname = "Tom";
        String lname = "Jones";
        String email = "tomjones@email.com";
        String position = "Scout";
        String rank = "First Class";
        String meritBadge = "First Aid";

        Document scout = new Document("_id", new ObjectId());
        scout.append("firstName", fname)
                .append("lastName", lname)
                .append("email", email)
                .append("position", position)
                .append("rank", rank)
                .append("MeritBadge", meritBadge);
        scoutCollection.insertOne(scout);
    }

    private static void deleteScout(MongoCollection<Document> scoutCollection) {
        String fname = "Tom";
        String lname = "Jones";

        Bson filter = and(eq("firstName", fname), eq("lastName", lname));
        DeleteResult deleteResult = scoutCollection.deleteOne(filter);
        System.out.println(deleteResult);
    }

    private static void updateEmail(MongoCollection<Document> scoutCollection) {
        String fname = "Tom";
        String lname = "Jones";
        String email = "newtomjones@email.com";

        Bson filter = and(eq("firstName", fname), eq("lastName", lname));
        Bson updateOperation = set("email", email);
        UpdateResult updateResult = scoutCollection.updateOne(filter, updateOperation);
        System.out.println(updateResult);

    }

    private static void updatePosition(MongoCollection<Document> scoutCollection) {
        String fname = "Tom";
        String lname = "Jones";
        String position = "New Position";

        Bson filter = and(eq("firstName", fname), eq("lastName", lname));
        Bson updateOperation = set("position", position);
        UpdateResult updateResult = scoutCollection.updateOne(filter, updateOperation);
        System.out.println(updateResult);

    }

    private static void updateRank(MongoCollection<Document> scoutCollection) {
        String fname = "Tom";
        String lname = "Jones";
        String rank = "New Rank";

        Bson filter = and(eq("firstName", fname), eq("lastName", lname));
        Bson updateOperation = set("rank", rank);
        UpdateResult updateResult = scoutCollection.updateOne(filter, updateOperation);
        System.out.println(updateResult);

    }

    private static void updateMeritBadge(MongoCollection<Document> scoutCollection) {
        String fname = "Tom";
        String lname = "Jones";
        String meritBadge = "New Merit Badge";

        Bson filter = and(eq("firstName", fname), eq("lastName", lname));
        Bson updateOperation = set("MeritBadge", meritBadge);
        UpdateResult updateResult = scoutCollection.updateOne(filter, updateOperation);
        System.out.println(updateResult);

    }

    public static final String wrap(String toWrap) {
        String string = new String();
        String[] words = toWrap.split(" ");

        int currentLineLength = 0;
        for (String word : words) {

            // Wrap with \
            if(word.contains("\\")) {
                while(word.contains("\\"))
                    word = word.replaceFirst("\\\\", "\n");
                string += (word + " ");
                currentLineLength = 0;
                continue;
            }
            // Tab with /t
            if(word.contains("/t")) {
                while(word.contains("/t"))
                    word = word.replaceFirst("/t", "\t");
                string += (word + " ");
                continue;
            }
            //string += (word + " ");
            // Auto warping
            if(currentLineLength + word.length() + 1 <= 100) {
                string += (word + " ");
                currentLineLength += word.length() + 1;
            }
            else {
                string += ("\n" + word + " ");
                currentLineLength = word.length() + 1;
            }
        }
        return string.trim();
    }
}
