import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';

import {Observable} from "rxjs";

import {Todo} from './todo';
import {environment} from "../../environments/environment";


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

    getTodoById(id: string): Observable<Todo> {
        return this.http.get<Todo>(this.todoUrl + "/" + id);
    }

    /*
    //This method looks lovely and is more compact, but it does not clear previous searches appropriately.
    //It might be worth updating it, but it is currently commented out since it is not used (to make that clear)
    getUsersByCompany(userCompany?: string): Observable<User> {
        this.userUrl = this.userUrl + (!(userCompany == null || userCompany == "") ? "?company=" + userCompany : "");
        console.log("The url is: " + this.userUrl);
        return this.http.request(this.userUrl).map(res => res.json());
    }
    */

    filterByCategory(todoCategory?: string): void {
        if(!(todoCategory == null || todoCategory == "")){
            if (this.todoUrl.indexOf('category=') !== -1){
                //there was a previous search by company that we need to clear
                let start = this.todoUrl.indexOf('category=');
                let end = this.todoUrl.indexOf('&', start);
                this.todoUrl = this.todoUrl.substring(0, start-1) + this.todoUrl.substring(end+1);
            }
            if (this.todoUrl.indexOf('&') !== -1) {
                //there was already some information passed in this url
                this.todoUrl += 'category=' + todoCategory + '&';
            }
            else {
                //this was the first bit of information to pass in the url
                this.todoUrl += "?category=" + todoCategory + "&";
            }
        }
        else {
            //there was nothing in the box to put onto the URL... reset
            if (this.todoUrl.indexOf('category=') !== -1){
                let start = this.todoUrl.indexOf('category=');
                let end = this.todoUrl.indexOf('&', start);
                if (this.todoUrl.substring(start-1, start) === '?'){
                    start = start-1
                }
                this.todoUrl = this.todoUrl.substring(0, start) + this.todoUrl.substring(end+1);
            }
        }
    }

    /*
    This needs to be thought about further, about how we will use adding an additional ID system???
    Currently, it is only using owner, category, status, and body, but not id.
    */

    addNewTodo(owner: string, category: string, status: string, body: string): Observable<Boolean> {
        const contense = {owner:owner, category:category, status:status, body:body};
        console.log(contense);

        //Send post request to add a new user with the user data as the body with specified headers.
        return this.http.post<Boolean>(this.todoUrl + "/new", body);
    }
}
