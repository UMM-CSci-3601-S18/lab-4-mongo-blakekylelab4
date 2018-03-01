package umm3601.todo;

import com.google.gson.Gson;
import com.mongodb.*;

import java.util.*;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import com.mongodb.client.MongoCursor;
import com.mongodb.util.JSON;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import com.mongodb.util.JSON;
import org.bson.Document;
import org.bson.types.ObjectId;
import java.util.Arrays;
import java.util.logging.Filter;

import static com.mongodb.client.model.Aggregates.addFields;
import static com.mongodb.client.model.Aggregates.unwind;
import org.bson.types.ObjectId;

import javax.print.DocFlavor;

import static com.mongodb.client.model.Filters.eq;

public class TodoController  {

    private final Gson gson;
    private MongoDatabase database;
    private final MongoCollection<Document> todoCollection;
    private final MongoCollection<Document> todoCollection2 = null;


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






    public String getTodoSummary() {

    ArrayList<Document> docs = new ArrayList<>();
        Document doc = new Document();

        float count = todoCollection.count();
        float percent = 100/count;



        AggregateIterable<Document> subTodos = todoCollection.aggregate(
            Arrays.asList(
                Aggregates.match(Filters.eq("status", true)),
                Aggregates.group("$status", Accumulators.sum("subtotal", 1)

                ), Aggregates.project(Projections.fields(Projections.computed("percentageStatusComplete", "$subtotal"), Projections.include("subtotal")))




            ));
        AggregateIterable<Document> subOwner = todoCollection.aggregate(
            Arrays.asList(
                Aggregates.match(Filters.eq("status", true)),
                Aggregates.group("$owner", Accumulators.sum("subtotal", 1)

                ), Aggregates.project(Projections.fields(Projections.computed("percentageCompleteOwner", "$subtotal"), Projections.include("subtotal")))




            ));
        AggregateIterable<Document> subCategory = todoCollection.aggregate(
            Arrays.asList(
                Aggregates.match(Filters.eq("status", true)),
                Aggregates.group("$category", Accumulators.sum("subtotal", 1)

                ), Aggregates.project(Projections.fields(Projections.computed("percentageCompleteCategory", "$subtotal"), Projections.include("subtotal")))




            ));


        MongoCursor<Document> _sub_iterator = subTodos.iterator();
        MongoCursor<Document> owner_iterator = subOwner.iterator();
        MongoCursor<Document> cat_iterator = subCategory.iterator();

        Document summary = new Document();
        summary.append("Total number of Todos", count);
        docs.add(summary);

        while (_sub_iterator .hasNext()) {
            Document next = _sub_iterator .next();



            docs.add(new Document("Statusvalue", next.getBoolean("_id")).append("percentageComplete",(next.getInteger("percentageStatusComplete")/count) ).append("subtotal",next.getInteger("subtotal") )
            );

        }


        while (owner_iterator.hasNext()) {
            Document next = owner_iterator.next();



            docs.add(new Document("owner", next.getString("_id")).append("percentageComplete",(next.getInteger("percentageCompleteOwner")/count) ).append("subtotal",next.getInteger("subtotal") )
            );

        }


        while (cat_iterator.hasNext()) {
            Document next = cat_iterator.next();



            docs.add(new Document("owner", next.getString("_id")).append("percentageComplete",(next.getInteger("percentageCompleteCategory")/count) ).append("subtotal",next.getInteger("subtotal") )
            );
        }
       // System.out.print(JSON.serialize(summary));
//        Document summaryDoc = new Document();
//        for(int i =0; i < docs.size(); i++) {
//            summaryDoc.append("Total Todos Complete", )
//
//        }

//        //count the subtotals of the owners
//        AggregateIterable<Document> subOwner = todoCollection.aggregate(
//            Arrays.asList(
//                Aggregates.group("$owner", Accumulators.sum("count", 1))
//            )
//        );
//
//        //filterByowner with status being true
//        AggregateIterable<Document> finOwner = todoCollection.aggregate(
//            Arrays.asList(
//                Aggregates.match(Filters.eq("status", "true")),
//                Aggregates.group("$owner", Accumulators.sum("count", 1))
//
//
//
//            ));
//
//        System.out.println(JSON.serialize(finOwner));
//        AggregateIterable<Document> catOwner = todoCollection.aggregate(
//            Arrays.asList(
//                Aggregates.match(Filters.eq("category", "homework")),
//                Aggregates.group("$owner", Accumulators.sum("count", 1),
//                    Accumulators.sum("percent", percent))
//
//            )
//        );
//        System.out.println(JSON.serialize(catOwner));
//
//        AggregateIterable<Document> bodyOwner = todoCollection.aggregate(
//            Arrays.asList(
//                Aggregates.match(Filters.eq("body", "sunt")),
//                Aggregates.group("$owner", Accumulators.sum("count", 1),
//                    Accumulators.sum("percent", percent))
//            )
//        );

//        //Document summary = new Document("percentToDosComplete:", );
//
//        //I am aware that doing this can lose data, but we are working with a small dataset.
//        total1 = new Long(todoCollection.count());
//        //Total number of todos
//       total = total1.floatValue();
//
//
//       //grabbing all completed todos
//        AggregateIterable<Document> CompleteStatus = todoCollection.aggregate(
//            Arrays.asList(
//                Aggregates.match(Filters.eq("status", "true")),
//                Aggregates.group("$status" , Accumulators.sum("count", 1)
//                    )
//            ));
//
//        AggregateIterable<Document>   toReturn = todoCollection.aggregate(
//            Arrays.asList(
//                Aggregates.match(Filters.eq("status", String.valueOf(true))),
//                Aggregates.group("status", Accumulators.sum("count", 1)),
//                Aggregates.project(
//                    Projections.fields(
//                        Projections.excludeId()
//
//
//
//                    )
//                )
//            )
//        );
//
//            System.out.println(JSON.serialize(toReturn));
//
//        MongoCursor<Document> status_iterator = CompleteStatus.iterator();
//
//        while (status_iterator.hasNext()) {
//            Document next = status_iterator.next();
//          System.out.println(next.getString("_id"));
//            System.out.println(next.getInteger("count"));
//
//        }



        return  JSON.serialize(docs);
    }


    public String summaryHelper(String paramName, String valueName, boolean status, float grandTotal) {
        String total = null;
        float percentage = 100;
        float subTotal;
        AggregateIterable<Document> toReturn;
        String stringToReturn;
        toReturn = todoCollection.aggregate(
            Arrays.asList(
                Aggregates.match(Filters.eq("status", String.valueOf(status))),
                Aggregates.group(paramName, Accumulators.sum( valueName, 1)),
                Aggregates.project(
                    Projections.fields(
                        Projections.excludeId(),
                        Projections.exclude(paramName)


                    )
                )
            )
        );
        MongoCursor<Document> toreturn_iterator = toReturn.iterator();

        while (toreturn_iterator.hasNext()) {
            Document next = toreturn_iterator.next();
             total = next.getString(valueName);

            break;


        }

       subTotal = (Float.valueOf(total)/grandTotal)*percentage;




        stringToReturn = JSON.serialize(toReturn.first());
        System.out.println(stringToReturn);
        return stringToReturn;

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

        Document filterDoc = new Document();



        //Filter by Owner
        if (queryParams.containsKey("owner")) {
            String targetOwner = (queryParams.get("owner")[0]);

            Document contentRegQuery = new Document();
            contentRegQuery.append("$regex", targetOwner);
            contentRegQuery.append("$options", "i");

            filterDoc = filterDoc.append("owner", contentRegQuery);

        }



        //These are not needed but its used for testing
        //Filter by Category
        if (queryParams.containsKey("category")) {
            String targetCategory = (queryParams.get("category")[0]);

            Document contentRegQuery = new Document();
            contentRegQuery.append("$regex", targetCategory);
            contentRegQuery.append("$options", "i");
            filterDoc = filterDoc.append("category", contentRegQuery);
        }
        //Filter by Status
        if (queryParams.containsKey("status")) {
            String targetCategory = (queryParams.get("status")[0]);

            Document contentRegQuery = new Document();
            contentRegQuery.append("$regex", targetCategory);
            contentRegQuery.append("$options", "i");
            filterDoc = filterDoc.append("status", contentRegQuery);
        }

        //Filter by Body
        if (queryParams.containsKey("body")) {

            String targetCategory = queryParams.get("body")[0];

            Document contentRegQuery = new Document();
            contentRegQuery.append("$regex", targetCategory);
            contentRegQuery.append("$options", "i");
            filterDoc = filterDoc.append("body", contentRegQuery);

        }



        //FindIterable comes from mongo, Document comes from Gson
        FindIterable<Document> matchingTodos = todoCollection.find(filterDoc);



        return JSON.serialize(matchingTodos);
    }


    /**Helper method which appends received user information to the to-be added document
     /**
     *
     * @param owner
     * @param category
     * @param status
     * @param body
     * @return boolean after successfully or unsuccessfully adding a user
     */
    public String addNewTodo(String owner, String category, String body, String status) {

        Document newTodo = new Document();
        newTodo.append("owner", owner);
        newTodo.append("status", status);
        newTodo.append("body", body);
        newTodo.append("category", category);



        try {
            todoCollection.insertOne(newTodo);
            ObjectId id = newTodo.getObjectId("_id");
            System.err.println("Successfully added new todo [_id=" + id + ", owner=" + owner + ", category=" + category + " body=" + body + " status=" + status + ']');
            // return JSON.serialize(newUser);
            return JSON.serialize(id);
        } catch(MongoException me) {
            me.printStackTrace();
            return null;
        }
    }
    public static void main(String[] args) {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase todoDatabase = mongoClient.getDatabase("dev");
        TodoController todoController = new TodoController(todoDatabase);

        System.out.println(todoController.getTodoSummary());
    }

}
