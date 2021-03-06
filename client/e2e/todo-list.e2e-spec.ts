import {TodoPage} from './todo-list.po';
import {browser, protractor} from 'protractor';
import {Key} from "selenium-webdriver";

let origFn = browser.driver.controlFlow().execute;

// https://hassantariqblog.wordpress.com/2015/11/09/reduce-speed-of-angular-e2e-protractor-tests/
browser.driver.controlFlow().execute = function () {
    let args = arguments;

    // queue 100ms wait between test
    // This delay is only put here so that you can watch the browser do its thing.
    // If you're tired of it taking long you can remove this call
    origFn.call(browser.driver.controlFlow(), function () {
        return protractor.promise.delayed(100);
    });

    return origFn.apply(browser.driver.controlFlow(), args);
};

describe('Todo list', () => {
    let page: TodoPage;

    beforeEach(() => {
        page = new TodoPage();
    });

    // it('should get and highlight Todos title attribute ', () => {
    //     page.navigateTo();
    //     expect(page.getTodoTitle()).toEqual('Todos');
    // });

    // it('should type something in filter owner box, click submit and check that it returned correct element', () => {
    //     page.navigateTo();
    //
    //     page.clickInput();
    //     expect(page.getUniqueTodo("58af3a600343927e48e87215")).toEqual("Blanche");
    // //     //page.backspace();
    // //     //page.typeAName("lynn");
    // //    // expect(page.getUniqueUser("lynnferguson@niquent.com")).toEqual("Lynn Ferguson");
    //  });




    it('should type in the body and check that an element contains that search element', () =>{
       page.navigateTo();
       page.typeABody('ipsum');
       expect(page.todoBodyClickAndGet()).toContain('Ipsum');
    });


    it("Should allow us to filter users based on company", ()=>{
        page.navigateTo();
        page.getOwner("Fry");
        page.getTodos();

        // it('should type in the body and check that an element contains that search element', () =>{
    //     page.navigateTo();
    //     page.selectComplete();
    //
    //     expect(page.todoStatusClickAndGet()).toContain('true');
    // });

    // it('should click category', () => {
    //     page.navigateTo();
    //     page.clickInput();
    //
    //     expect(page.getUniqueTodo("58af3a600343927e48e87210")).toEqual("Fry");
    //     //page.backspace();
    //     //page.typeAName("lynn");
    //     // expect(page.getUniqueUser("lynnferguson@niquent.com")).toEqual("Lynn Ferguson");
    // });







    // it('should click on the category and select software design', () => {
    //     page.navigateTo();
    //     page.getTodoByCategory();
    //     page.selectDownKey();
    //     page.selectDownKey();
    //     page.enter();
    //
    //     expect(page.getUniqueTodo("58af3a600343927e48e8720f")).toEqual("Blanche, 58af3a600343927e48e8720f ");
    // });
    //
    //
    //
    // //Stopped here
    // it("Should click the radio button and select complete", ()=>{
    //     page.navigateTo();
    //     page.selectComplete();
    //
    //     expect(page.getUniqueTodo("58af3a600343927e48e87211")).toEqual("Fry");
    //
    // });
    //
    // it("Should click the radio button and select complete", ()=>{
    //     page.navigateTo();
    //     page.selectComplete();
    //
    //     expect(page.getUniqueOwner("Fry")).toEqual("Fry");
    //
    // });
    //
    //
    // it("Should allow us to filter users based on company", ()=>{
    //     page.navigateTo();
    //     page.getCompany("o");
    //     page.getUsers().then(function(users) {
    //         expect(users.length).toBe(4);
    //     });
    //     expect(page.getUniqueUser("conniestewart@ohmnet.com")).toEqual("Connie Stewart");
    //     expect(page.getUniqueUser("stokesclayton@momentia.com")).toEqual("Stokes Clayton");
    //     expect(page.getUniqueUser("kittypage@surelogic.com")).toEqual("Kitty Page");
    //     expect(page.getUniqueUser("margueritenorton@recognia.com")).toEqual("Marguerite Norton");
    // });
    //
    // it("Should allow us to clear a search for company and then still successfully search again", ()=> {
    //     page.navigateTo();
    //     page.getCompany("m");
    //     page.getUsers().then(function(users) {
    //         expect(users.length).toBe(2);
    //     });
    //     page.clickClearCompanySearch();
    //     page.getUsers().then(function(users) {
    //         expect(users.length).toBe(10);
    //     });
    //     page.getCompany("ne");
    //     page.getUsers().then(function(users) {
    //         expect(users.length).toBe(3);
    //     });
    // })
})});
