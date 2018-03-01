import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';

import {Observable} from 'rxjs/Observable';

import {Todo} from './todo';
import {environment} from '../../environments/environment';



@Injectable()
export class TodoListService {
    readonly baseUrl: string = environment.API_URL + 'todos';
    private todoUrl: string = this.baseUrl;

    constructor(private http: HttpClient) {
    }

    getTodos(todoCategory?: string): Observable<Todo[]> {
        this.filterByCategory(todoCategory);
        return this.http.get<Todo[]>(this.todoUrl);
    }

    getTodosByOwner(todoOwner?: string): Observable<Todo[]>{
        this.filterByOwner(todoOwner);
        return this.http.get<Todo[]>(this.todoUrl);
    }

    getTodoById(id: string): Observable<Todo> {
        return this.http.get<Todo>(this.todoUrl + '/' + id);
    }

    filterByOwner(todoOwner?: string): void {
        if (!(todoOwner == null || todoOwner === '')) {


            if (this.parameterPresent('owner=') ) {

                // there was a previous search by category that we need to clear
                this.removeParameter('owner=');
            }
            if (this.todoUrl.indexOf('?') !== -1) {

                // there was already some information passed in this url
                this.todoUrl += 'owner=' + todoOwner + '&';
            } else {
                // this was the first bit of information to pass in the url
                this.todoUrl += '?owner=' + todoOwner + '&';
            }

        } else {

            // there was nothing in the box to put onto the URL... reset
            if (this.parameterPresent('owner=')) {
                let start = this.todoUrl.indexOf('owner=');
                const end = this.todoUrl.indexOf('&', start);
                if (this.todoUrl.substring(start - 1, start) === '?') {
                    start = start - 1;
                }
                this.todoUrl = this.todoUrl.substring(0, start) + this.todoUrl.substring(end + 1);
            }
        }
    }


    filterByCategory(todoCategory?: string): void {
        if (!(todoCategory == null || todoCategory === '')) {


            if (this.parameterPresent('category=') ) {

                // there was a previous search by category that we need to clear
                this.removeParameter('category=');
            }
            if (this.todoUrl.indexOf('?') !== -1) {

                // there was already some information passed in this url
                this.todoUrl += 'category=' + todoCategory + '&';
            } else {
                // this was the first bit of information to pass in the url
                this.todoUrl += '?category=' + todoCategory + '&';
            }

        } else {

            // there was nothing in the box to put onto the URL... reset
            if (this.parameterPresent('category=')) {
                let start = this.todoUrl.indexOf('category=');
                const end = this.todoUrl.indexOf('&', start);
                if (this.todoUrl.substring(start - 1, start) === '?') {
                    start = start - 1;
                }
                this.todoUrl = this.todoUrl.substring(0, start) + this.todoUrl.substring(end + 1);
            }
        }
    }

    private parameterPresent(searchParam: string) {

        return this.todoUrl.indexOf(searchParam) !== -1;
    }

    // remove the parameter and, if present, the &
    private removeParameter(searchParam: string) {
        const start = this.todoUrl.indexOf(searchParam);
        let end = 0;
        if (this.todoUrl.indexOf('&') !== -1) {
            end = this.todoUrl.indexOf('&', start) + 1;
        } else {
            end = this.todoUrl.indexOf('&', start);

        }

        this.todoUrl = this.todoUrl.substring(0, start) + this.todoUrl.substring(end);

    }

    addNewTodo(newTodo: Todo): Observable<{'$oid': string}> {
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type': 'application/json'
            }),
        };

        // Send post request to add a new todos with the todos data as the body with specified headers.
        return this.http.post<{'$oid': string}>(this.todoUrl + '/new', newTodo, httpOptions);
    }
}
