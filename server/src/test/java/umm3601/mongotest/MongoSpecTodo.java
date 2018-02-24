package umm3601.mongotest;

import com.mongodb.MongoClient;
import com.mongodb.client.*;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;
import static org.junit.Assert.*;

/**
 * Some simple "tests" that demonstrate our ability to
 * connect to a Mongo database and run some basic queries
 * against it.
 *
 * Note that none of these are actually tests of any of our
 * code; they are mostly demonstrations of the behavior of
 * the MongoDB Java libraries. Thus if they test anything,
 * they test that code, and perhaps our understanding of it.
 *
 * To test "our" code we'd want the tests to confirm that
 * the behavior of methods in things like the TodoController
 * do the "right" thing.
 *
 * Created by Kyle Foss on 24/2/17.
 */
public class MongoSpecTodo {

    private MongoCollection<Document> todoDocuments;

    @Before
    public void clearAndPopulateDB() {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase db = mongoClient.getDatabase("test");
        todoDocuments = db.getCollection("todos");
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
        todoDocuments.insertMany(testTodos);
    }

    private List<Document> intoList(MongoIterable<Document> documents) {
        List<Document> todos = new ArrayList<>();
        documents.into(todos);
        return todos;
    }

    private int countTodos(FindIterable<Document> documents) {
        List<Document> todos = intoList(documents);
        return todos.size();
    }

    @Test
    public void shouldBeThreeTodos() {
        FindIterable<Document> documents = todoDocuments.find();
        int numberOfTodos = countTodos(documents);
        assertEquals("Should be a total of 3 todos", 3, numberOfTodos);
    }

    @Test
    public void shouldBeOneFry() {
        FindIterable<Document> documents = todoDocuments.find(eq("owner", "Fry"));
        int numberOfTodos = countTodos(documents);
        assertEquals("Should be 1 Fry", 1, numberOfTodos);
    }

    @Test
    public void categoryTest() {
        FindIterable<Document> documents = todoDocuments.find(eq("category", "homework"));
        int numberOfTodos = countTodos(documents);
        assertEquals("Should be 2", 2, numberOfTodos);
    }

    @Test
    public void bodyTest() {
        FindIterable<Document> documents = todoDocuments.find(eq("body", "In sunt ex non tempor cillum commodo amet incididunt anim qui commodo quis. Cillum non labore ex sint esse."));
        int numberOfTodos = countTodos(documents);
        assertEquals("Should be 2", 1, numberOfTodos);
    }
    @Test
    public void statusTest() {
        FindIterable<Document> documents = todoDocuments.find(eq("status", "true"));
        int numberOfTodos = countTodos(documents);
        assertEquals("Should be 2", 2, numberOfTodos);
    }

    @Test
    public void categoryTestSortedByName() {
        FindIterable<Document> documents
            = todoDocuments.find()
            .sort(Sorts.ascending("category"));
        List<Document> docs = intoList(documents);
        assertEquals("Should be 3", 3, docs.size());
        assertEquals("First should be Jamie", "homework", docs.get(0).get("category"));
        assertEquals("Second should be Pat", "Workman", docs.get(1).get("owner"));
    }



    @Test
    public void justOwnerAndCategoryAndSort() {
        FindIterable<Document> documents
            = todoDocuments.find().projection(fields(include("owner", "category"))).sort(Sorts.ascending("owner"));
        List<Document> docs = intoList(documents);
        assertEquals("Should be 3", 3, docs.size());
        assertEquals("First should be Blanche", "Blanche", docs.get(0).get("owner"));
        assertEquals("Second todo should have the category ","homework", docs.get(1).get("category"));
        assertNull("First shouldn't have 'body'", docs.get(0).get("body"));
        assertNotNull("First should have '_id'", docs.get(0).get("_id"));
    }

    @Test
    public void justOwnerAndBodyNoId() {
        FindIterable<Document> documents
            = todoDocuments.find()
            .projection(fields(include("owner", "body"), excludeId())).sort(Sorts.ascending("owner"));
        List<Document> docs = intoList(documents);
        assertEquals("Should be 3", 3, docs.size());
        assertEquals("First should be Chris", "Blanche", docs.get(0).get("owner"));
        assertNotNull("First should have owner", docs.get(0).get("owner"));
        assertNotNull("First should have Body", docs.get(0).get("body"));
        assertNull("First shouldn't have 'company'", docs.get(0).get("status"));
        assertNull("First should not have '_id'", docs.get(0).get("_id"));
    }

    @Test
    public void justCategoryAndStausNoIdSortedByCategory() {
        FindIterable<Document> documents
            = todoDocuments.find()
            .sort(Sorts.ascending("category"))
            .projection(fields(include("category", "status"), excludeId()));
        List<Document> docs = intoList(documents);
        assertEquals("Should be 3", 3, docs.size());
        assertEquals("First should be homework", "homework", docs.get(0).get("category"));
        assertNotNull("First should have category", docs.get(0).get("category"));
        assertNotNull("First should have status", docs.get(0).get("status"));
        assertNull("First shouldn't have 'body'", docs.get(0).get("body"));
        assertNull("First shouldn't have 'owner'", docs.get(0).get("owner"));
        assertNull("First should not have '_id'", docs.get(0).get("_id"));
    }

    //come back to this test later


//    @Test
//    public void categoryCounts() {
//        AggregateIterable<Document> documents
//            = todoDocuments.aggregate(
//            Arrays.asList(
//                        /*
//                         * Groups data by the "category" field, and then counts
//                         * the number of documents with each given category.
//                         * This creates a new "constructed document" that
//                         * has "category" as it's "_id", and the count as the
//                         * "categoryCount" field.
//                         */
//                Aggregates.group("$category",
//                    Accumulators.sum("categoryCount", 1)),
//                Aggregates.sort(Sorts.ascending("_id"))
//            )
//        );
//        List<Document> docs = intoList(documents);
//        assertEquals("Should be two distinct ages", 2, docs.size());
//        assertEquals(docs.get(0).get("_id"), 25);
//        assertEquals(docs.get(0).get("ageCount"), 1);
//        assertEquals(docs.get(1).get("_id"), 37);
//        assertEquals(docs.get(1).get("ageCount"), 2);
//    }



}
