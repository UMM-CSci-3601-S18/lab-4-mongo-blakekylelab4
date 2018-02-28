package umm3601.todo;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.*;
import org.bson.codecs.*;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.json.JsonReader;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * JUnit tests for the TodoController.
 *
 * Created by Kyle Foss on 22/2/18.
 */
public class TodoControllerSpec
{
    private TodoController todoController;
    private ObjectId tedsId;
    @Before
    public void clearAndPopulateDB() throws IOException {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase db = mongoClient.getDatabase("test");
        MongoCollection<Document> todoDocuments = db.getCollection("todos");
        todoDocuments.drop();
        List<Document> testTodos = new ArrayList<>();
        testTodos.add(Document.parse("{\n" +
            "                    owner: \"Blanche\",\n" +
            "                    status: \"true\",\n" +
            "                    body: \"In sunt ex non tempor cillum commodo amet incididunt anim qui commodo quis. Cillum non labore ex sint esse.\",\n" +
            "                    category: \"software design\"\n" +
            "                }"));
        testTodos.add(Document.parse("{\n" +
            "                    owner: \"Fry\",\n" +
            "                    status: \"false\",\n" +
            "                    body: \"Veniam ut ex sit voluptate Lorem. Laboris ipsum nulla proident aute culpa esse aute pariatur velit deserunt deserunt cillum officia dolore.\",\n" +
            "                    category: \"homework\"\n" +
            "                }"));
        testTodos.add(Document.parse("{\n" +
            "                    owner: \"Workman\",\n" +
            "                    status: \"true\",\n" +
            "                    body: \"Eiusmod commodo officia amet aliquip est ipsum nostrud duis sunt voluptate mollit excepteur. Sunt non in pariatur et culpa est sunt.\",\n" +
            "                    category: \"homework\"\n" +
            "                }"));

        tedsId = new ObjectId();
        BasicDBObject ted = new BasicDBObject("_id", tedsId);
        ted = ted.append("owner", "Ted")
            .append("status", "true")
            .append("body", "iusmod")
            .append("category", "video games");



        todoDocuments.insertMany(testTodos);
        todoDocuments.insertOne(Document.parse(ted.toJson()));

        // It might be important to construct this _after_ the DB is set up
        // in case there are bits in the constructor that care about the state
        // of the database.
        todoController = new TodoController(db);
    }

    // http://stackoverflow.com/questions/34436952/json-parse-equivalent-in-mongo-driver-3-x-for-java
    private BsonArray parseJsonArray(String json) {
        final CodecRegistry codecRegistry
            = CodecRegistries.fromProviders(Arrays.asList(
            new ValueCodecProvider(),
            new BsonValueCodecProvider(),
            new DocumentCodecProvider()));

        JsonReader reader = new JsonReader(json);
        BsonArrayCodec arrayReader = new BsonArrayCodec(codecRegistry);

        return arrayReader.decode(reader, DecoderContext.builder().build());
    }

    private static String getOwner(BsonValue val) {
        BsonDocument doc = val.asDocument();
        return ((BsonString) doc.get("owner")).getValue();
    }

    @Test
    public void getAllTodos() {
        Map<String, String[]> emptyMap = new HashMap<>();
        String jsonResult = todoController.getTodos(emptyMap);
        BsonArray docs = parseJsonArray(jsonResult);

        assertEquals("Should be 4", 4, docs.size());
        List<String> owners = docs
            .stream()
            .map(TodoControllerSpec::getOwner)
            .sorted()
            .collect(Collectors.toList());
        List<String> expectedOwners = Arrays.asList("Blanche", "Fry", "Ted", "Workman");
        assertEquals("Owners should match", expectedOwners, owners);
    }

    //Need to take a look at this again


//    @Test
//    public void getOwners() {
//        Map<String, String[]> argMap = new HashMap<>();
//        argMap.put("body", new String[] { "[Veniam ut ex sit voluptate Lorem.]" });
//        String jsonResult = todoController.getTodos(argMap);
//        System.out.println(jsonResult);
//        BsonArray docs = parseJsonArray(jsonResult);
//
//
//        assertEquals("Wrong number of todos returned", 1, docs.size());
//        List<String> owners = docs
//            .stream()
//            .map(TodoControllerSpec::getOwner)
//            .sorted()
//            .collect(Collectors.toList());
//        List<String> expectedOwners = Arrays.asList("Blanche");
//        assertEquals("Owners should match", expectedOwners, owners);
//    }

    @Test
    public void getTedById() {
        String jsonResult = todoController.getTodo(tedsId.toHexString());
        Document ted = Document.parse(jsonResult);
        assertEquals("Owner should match", "Ted", ted.get("owner"));
        String noJsonResult = todoController.getTodo(new ObjectId().toString());
        assertNull("No owner should match",noJsonResult);

    }

    @Test
    public void addTodoTest(){
        String bool = todoController.addNewTodo("Andy","homework","false", "tempor cillum");
       assertNotNull("Add new todo should return true when a todo is added", bool);
        Map<String, String[]> argMap = new HashMap<>();
        argMap.put("category", new String[] { "homework" });
        String jsonResult = todoController.getTodos(argMap);
        BsonArray docs = parseJsonArray(jsonResult);
        System.out.println(docs);
        List<String> owner = docs
            .stream()
            .map(TodoControllerSpec::getOwner)
            .sorted()
            .collect(Collectors.toList());
        assertEquals("Should return the owner of new todo", "Andy", owner.get(0));
    }



    //This filter by owner on the server side
   @Test
    public void getTodoByOwner(){
        Map<String, String[]> argMap = new HashMap<>();
        //Mongo in TodoController is doing a regex search so can just take a Java Reg. Expression
        //This will search for the owner starting with an W
        argMap.put("owner", new String[] { "[W]" });
        String jsonResult = todoController.getTodos(argMap);
       System.out.println(jsonResult);
        BsonArray docs = parseJsonArray(jsonResult);
        assertEquals("Should be 2 todos", 1, docs.size());
        List<String> owner = docs
            .stream()
            .map(TodoControllerSpec::getOwner)
            .sorted()
            .collect(Collectors.toList());
        List<String> expectedOwner = Arrays.asList("Workman");
        assertEquals("Owners should match", expectedOwner, owner);

    }



}
