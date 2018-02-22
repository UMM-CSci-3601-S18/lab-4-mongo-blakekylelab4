package umm3601.todo;

import com.google.gson.Gson;
import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;
import org.bson.Document;
import org.bson.types.ObjectId;
import java.util.Iterator;
import java.util.Map;

import static com.mongodb.client.model.Filters.eq;

public class TodoController {

    private final Gson gson;
    private MongoDatabase database;
    private final MongoCollection<Document> todoCollection;

    /**
     * Construct a controller for users.
     *
     * @param database the database containing user data
     */
    public TodoController(MongoDatabase database) {
        gson = new Gson();
        this.database = database;
        todoCollection = database.getCollection("todos");
    }

    /**
     * Helper method that gets a single user specified by the `id`
     * parameter in the request.
     *
     * @param id the Mongo ID of the desired user
     * @return the desired user as a JSON object if the user with that ID is found,
     * and `null` if no user with that ID is found
     */
    public String getTodo(String id) {
        FindIterable<Document> jsonTodos
            = todoCollection
            .find(eq("_id", new ObjectId(id)));

        Iterator<Document> iterator = jsonTodos.iterator();
        if (iterator.hasNext()) {
            Document todo = iterator.next();
            return todo.toJson();
        } else {
            // We didn't find the desired user
            return null;
        }
    }


    /** Helper method which iterates through the collection, receiving all
     * documents if no query parameter is specified. If the age query parameter
     * is specified, then the collection is filtered so only documents of that
     * specified age are found.
     *
     * @param queryParams
     * @return an array of Users in a JSON formatted string
     */
    public String getTodos(Map<String, String[]> queryParams) {


        //Flittering on the Server Side rather than the client.
        Document filterDoc = new Document();

        //Filter by owner
        if (queryParams.containsKey("owner")) {
            String targetOwner = queryParams.get("owner")[0];
            filterDoc = filterDoc.append("owner", targetOwner);
        }

        //Filter by category
        if (queryParams.containsKey("category")) {
            String targetCategory = queryParams.get("company")[0];
            Document contentRegQuery = new Document();
            contentRegQuery.append("$regex", targetCategory);
            contentRegQuery.append("$options", "i");
            filterDoc = filterDoc.append("category", contentRegQuery);
        }

        //FindIterable comes from mongo, Document comes from Gson
        FindIterable<Document> matchingTodos = todoCollection.find(filterDoc);

        return JSON.serialize(matchingTodos);
    }


    /**
     * Helper method which appends received user information to the to-be added document
     *
     * @param owner
     * @param category
     * @param status
     * @param body
     * @return boolean after successfully or unsuccessfully adding a user
     */
    public String addNewTodo(String owner, String category, String status, String body) {

        Document newTodo = new Document();
        newTodo.append("owner", owner);
        newTodo.append("category", category);
        newTodo.append("stauts", status);
        newTodo.append("body", body);

        try {
            todoCollection.insertOne(newTodo);
            ObjectId id = newTodo.getObjectId("_id");
            System.err.println("Successfully added new todo [_id=" + id + ", owner=" + owner + ", category=" + category + " status=" + status + " body=" + body + ']');
            // return JSON.serialize(newUser);
            return JSON.serialize(id);
        } catch(MongoException me) {
            me.printStackTrace();
            return null;
        }
    }
}
