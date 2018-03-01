import {browser, element, by} from 'protractor';
import {Key} from "selenium-webdriver";

export class TodoPage {
    navigateTo() {
        return browser.get('/todos');
    }

    //http://www.assertselenium.com/protractor/highlight-elements-during-your-protractor-test-run/
    highlightElement(byObject) {
        function setStyle(element, style) {
            const previous = element.getAttribute('style');
            element.setAttribute('style', style);
            setTimeout(() => {
                element.setAttribute('style', previous);
            }, 200);
            return "highlighted";
        }

        return browser.executeScript(setStyle, element(byObject).getWebElement(), 'color: red; background-color: yellow;');
    }

    getTodoTitle() {
        let title = element(by.id('todo-list-title')).getText();
        this.highlightElement(by.id('todo-list-title'));

        return title;
    }

    typeAName(name: string) {
        let input = element(by.id('todoOwner'));
        input.click();
        input.sendKeys(name);
    }

    selectUpKey() {
        browser.actions().sendKeys(Key.ARROW_UP).perform();
    }

    selectDownKey(){
        browser.actions().sendKeys(Key.ARROW_DOWN).perform();
    }

    enter(){
        browser.actions().sendKeys(Key.ENTER).perform();
    }

    backspace(){
        browser.actions().sendKeys(Key.BACK_SPACE).perform();
    }

    getCompany(company:string){
        let input = element(by.id('userCompany'));
        input.click();
        input.sendKeys(company);
        let selectButton = element(by.id('submit'));
        selectButton.click();
    }

    getTodoByCategory() {
        let input = element(by.id('todoCategory'));
        input.click();
    }

    getUniqueTodo(id:string) {
        let todo = element(by.id(id)).getText();
        this.highlightElement(by.id(id));

        return todo;
    }

    getUsers() {
        return element.all(by.className('users'));
    }

    clickClearCompanySearch() {
        let input = element(by.id('companyClearSearch'));
        input.click();
    }
}
