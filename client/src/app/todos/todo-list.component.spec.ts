import {ComponentFixture, TestBed, async} from '@angular/core/testing';
import {Todo} from './todo';
import {TodoListComponent} from './todo-list.component';
import {TodoListService} from './todo-list.service';
import {Observable} from 'rxjs';
import {FormsModule} from '@angular/forms';
import {CustomModule} from '../custom.module';
import {MATERIAL_COMPATIBILITY_MODE} from '@angular/material';



describe('Todo list', () => {

    let todoList: TodoListComponent;
    let fixture: ComponentFixture<TodoListComponent>;

    let todoListServiceStub: {
        getTodos: () => Observable<Todo[]>
    };

    beforeEach(() => {
        // stub UserService for test purposes
        todoListServiceStub = {
            getTodos: () => Observable.of([
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
            ])
        };

        TestBed.configureTestingModule({
            imports: [CustomModule],
            declarations: [TodoListComponent],
            // providers:    [ UserListService ]  // NO! Don't provide the real service!
            // Provide a test-double instead
            providers: [{provide: TodoListService, useValue: todoListServiceStub},
                {provide: MATERIAL_COMPATIBILITY_MODE, useValue: true}]
        });
    });

    beforeEach(async(() => {
        TestBed.compileComponents().then(() => {
            fixture = TestBed.createComponent(TodoListComponent);
            todoList = fixture.componentInstance;
            fixture.detectChanges();
        });
    }));

    it('contains all the users', () => {
        expect(todoList.todos.length).toBe(3);
    });

    it('contains a todo with an owner named "Fry"', () => {
        expect(todoList.todos.some((todo: Todo) => todo.owner === 'Fry')).toBe(true);
    });

    it('contains a todo with an owner named "Workman"', () => {
        expect(todoList.todos.some((todo: Todo) => todo.owner === 'Workman')).toBe(true);
    });

    it("doesn't contain a todo with an owner named 'Santa'", () => {
        expect(todoList.todos.some((todo: Todo) => todo.owner === 'Santa')).toBe(false);
    });

    it("has two todos that have a catergory called 'homework'", () => {
        expect(todoList.todos.filter((todo: Todo) => todo.category === 'homework').length).toBe(2);
    });

    it("todo list filters by owner", () => {
        expect(todoList.filteredTodos.length).toBe(3);
        todoList.todoOwner = "a";
        let a : Observable<Todo[]> = todoList.refreshTodos();
        a.do(x => Observable.of(x))
            .subscribe(x =>
            {
                expect(todoList.filteredTodos.length).toBe(2);
            });
    });

    it('Todolist filters by category', () => {
        expect(todoList.filteredTodos.length).toBe(3);
        todoList.todoCategory = 'homework';
        let a : Observable<Todo[]> = todoList.refreshTodos();
        a.do(x => Observable.of(x))
            .subscribe(x =>
            {
                expect(todoList.filteredTodos.length).toBe(2);
            });
    });

    it('todo list filters by owner and category', () => {
        expect(todoList.filteredTodos.length).toBe(3);
        todoList.todoCategory = 'homework';
        todoList.todoOwner = 'Blanche';
        let a : Observable<Todo[]> = todoList.refreshTodos();
        a.do(x => Observable.of(x))
            .subscribe(x =>
            {
                expect(todoList.filteredTodos.length).toBe(1);
            });
    });

});

describe("Misbehaving Todo List", () => {
    let todoList: TodoListComponent;
    let fixture: ComponentFixture<TodoListComponent>;

    let todoListServiceStub: {
        getTodos: () => Observable<Todo[]>
    };

    beforeEach(() => {
        // stub UserService for test purposes
        todoListServiceStub = {
            getTodos: () => Observable.create(observer => {
                observer.error("Error-prone observable");
            })
        };

        TestBed.configureTestingModule({
            imports: [FormsModule, CustomModule],
            declarations: [TodoListComponent],
            providers: [{provide: TodoListService, useValue: todoListServiceStub},
                {provide: MATERIAL_COMPATIBILITY_MODE, useValue: true}]
        })
    });

    beforeEach(async(() => {
        TestBed.compileComponents().then(() => {
            fixture = TestBed.createComponent(TodoListComponent);
            todoList = fixture.componentInstance;
            fixture.detectChanges();
        });
    }));

    it("generates an error if we don't set up a UserListService", () => {
        // Since the observer throws an error, we don't expect users to be defined.
        expect(todoList.todos).toBeUndefined();
    });
});
