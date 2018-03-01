## Questions

1. :question: What do we do in the `Server` and `UserController` constructors
to set up our connection to the development database?
1. :question: How do we retrieve a user by ID in the `UserController.getUser(String)` method?

1. :question: How do we retrieve all the users with a given age 
in `UserController.getUsers(Map...)`? What's the role of `filterDoc` in that
method?

1. :question: What are these `Document` objects that we use in the `UserController`? 
Why and how are we using them?
1. :question: What does `UserControllerSpec.clearAndPopulateDb` do?
1. :question: What's being tested in `UserControllerSpec.getUsersWhoAre37()`?
How is that being tested?
1. :question: Follow the process for adding a new user. What role do `UserController` and 
`UserRequestHandler` play in the process?

## Your Team's Answers

1. The constructor for UserController takes in a database and grabs a collection from it. The server constructor takes in a port number and database and setup the client
2. The UserController.getUSer method take in an ID from a request and returns the desired user or null.
3. Filter docs holds the structure of the document we are looking for in a collection.
4. Documents are the json objects we store in collection in mongoDB. 
5. This is responisble for grabbing a mongo collection from the test database. The collection is cleared for testing our testing data.
6. THis is responible for testing if we can filter by age through a collection
7. The Usercontroler is responible for creating a new user when. The UserRequestHandler gets user info from a request from the server when the server receieves an api route, and then calls the addNewUser method

