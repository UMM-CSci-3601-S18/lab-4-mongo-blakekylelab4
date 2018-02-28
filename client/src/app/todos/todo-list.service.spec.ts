import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {TestBed} from '@angular/core/testing';
import {HttpClient} from '@angular/common/http';

import {Todo} from './todo';
import {TodoListService} from './todo-list.service';

describe('Todo list service: ', () => {
    // A small collection of test users
    const testTodos: Todo[] = [
        {
            _id: 'Blanche_id',
            owner: 'Blanche',
            status: 'false',
            body: 'Nisi eiusmod aliqua velit quis occaecat excepteur.',
            category: 'homework'
        },
        {
            _id: 'Workman_id',
            owner: 'Workman',
            status: 'false',
            body: 'In sunt ex non tempor cillum commodo amet incididunt anim qui commodo quis. Cillum non labore ex sint esse.',
            category: 'homework'
        },
        {
            _id: 'Fry_id',
            owner: 'Fry',
            status: 'true',
            body: 'Laborum incididunt nisi eiusmod aliquaut in ad. Commodo adipisicing sin.',
            category: 'video games'
        }
    ];
    const mTodos: Todo[] = testTodos.filter(todo =>
        todo.category.toLowerCase().indexOf('homework') !== -1
    );

    // We will need some url information from the userListService to meaningfully test company filtering;
    // https://stackoverflow.com/questions/35987055/how-to-write-unit-testing-for-angular-2-typescript-for-private-methods-with-ja
    let todoListService: TodoListService;
    let currentlyImpossibleToGenerateSearchTodoUrl: string;

    // These are used to mock the HTTP requests so that we (a) don't have to
    // have the server running and (b) we can check exactly which HTTP
    // requests were made to ensure that we're making the correct requests.
    let httpClient: HttpClient;
    let httpTestingController: HttpTestingController;

    beforeEach(() => {
        // Set up the mock handling of the HTTP requests
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule]
        });
        httpClient = TestBed.get(HttpClient);
        httpTestingController = TestBed.get(HttpTestingController);
        // Construct an instance of the service with the mock
        // HTTP client.
        todoListService = new TodoListService(httpClient);
    });

    afterEach(() => {
        // After every test, assert that there are no more pending requests.
        httpTestingController.verify();
    });

    it('getTodos() calls api/todos', () => {
        // Assert that the users we get from this call to getUsers()
        // should be our set of test users. Because we're subscribing
        // to the result of getUsers(), this won't actually get
        // checked until the mocked HTTP request "returns" a response.
        // This happens when we call req.flush(testUsers) a few lines
        // down.
        todoListService.getTodos().subscribe(
            todos => expect(todos).toBe(testTodos)
        );

        // Specify that (exactly) one request will be made to the specified URL.
        const req = httpTestingController.expectOne(todoListService.baseUrl);
        // Check that the request made to that URL was a GET request.
        expect(req.request.method).toEqual('GET');
        // Specify the content of the response to that request. This
        // triggers the subscribe above, which leads to that check
        // actually being performed.
        req.flush(testTodos);
    });

    it('getTodos(todoCategory) adds appropriate param string to called URL', () => {
        todoListService.getTodos('homework').subscribe(
            todos => expect(todos).toEqual(mTodos)
        );

        const req = httpTestingController.expectOne(todoListService.baseUrl + '?category=homework&');
        expect(req.request.method).toEqual('GET');
        req.flush(mTodos);
    });

    it('filterByCategory(todoCategory) deals appropriately with a URL that already had a category', () => {
        currentlyImpossibleToGenerateSearchTodoUrl = todoListService.baseUrl + '?category=f&something=k&';
        todoListService['todoUrl'] = currentlyImpossibleToGenerateSearchTodoUrl;
        todoListService.filterByCategory('homework');
        expect(todoListService['todoUrl']).toEqual(todoListService.baseUrl + '?something=k&category=homework&');
    });

    it('filterByCategory(todoCategory) deals appropriately with a URL that already had some filtering, but no category', () => {
        currentlyImpossibleToGenerateSearchTodoUrl = todoListService.baseUrl + '?something=k&';
        todoListService['todoUrl'] = currentlyImpossibleToGenerateSearchTodoUrl;
        todoListService.filterByCategory('homework');
        expect(todoListService['todoUrl']).toEqual(todoListService.baseUrl + '?something=k&category=homework&');
    });

    it('filterByCategory(todoCategory) deals appropriately with a URL has the keyword category, but nothing after the =', () => {
        currentlyImpossibleToGenerateSearchTodoUrl = todoListService.baseUrl + '?category=&';
        todoListService['todoUrl'] = currentlyImpossibleToGenerateSearchTodoUrl;
        todoListService.filterByCategory('');
        expect(todoListService['todoUrl']).toEqual(todoListService.baseUrl + '');
    });

    //This is a very ugly test, but I don't think there's a way to cover this branch without changing
    //code siginificantly in todo-listservice.
    it('filterByCategory(todoCategory) deals appropriately with a URL has the keyword category, but nothing after the = without the &', () => {
        currentlyImpossibleToGenerateSearchTodoUrl = todoListService.baseUrl +'?category=';
        todoListService['todoUrl'] = currentlyImpossibleToGenerateSearchTodoUrl;
        todoListService.filterByCategory('z');
     
        expect(todoListService['todoUrl']).toEqual(todoListService.baseUrl + '?http://localhost:4567/api/todos?category=category=z&');
    });


    it('getTodoById() calls api/todos/id', () => {
        const targetTodo: Todo = testTodos[1];
        const targetId: string = targetTodo._id;
        todoListService.getTodoById(targetId).subscribe(
            todo => expect(todo).toBe(targetTodo)
        );

        const expectedUrl: string = todoListService.baseUrl + '/' + targetId;
        const req = httpTestingController.expectOne(expectedUrl);
        expect(req.request.method).toEqual('GET');
        req.flush(targetTodo);
    });

    it('adding a user calls api/todos/new', () => {
        const jesse_id = { '$oid': 'jesse_id' };
        const newTodo: Todo = {
            _id: '',
            owner: 'Jesse',
            status: 'false',
            body: 'Smithsonian',
            category: 'jesse@stuff.com'
        };

        todoListService.addNewTodo(newTodo).subscribe(
            id => {
                expect(id).toBe(jesse_id);
            }
        );

        const expectedUrl: string = todoListService.baseUrl + '/new';
        const req = httpTestingController.expectOne(expectedUrl);
        expect(req.request.method).toEqual('POST');
        req.flush(jesse_id);
    });
});
