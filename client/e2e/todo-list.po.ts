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

    todoBodyClickAndGet() {
        let elem = element.all(by.css('.accordion')).first();
        let container = element.all(by.css('.todo')).first();
        container.click();
        return element.all(by.css('.todoBodyThing')).first().getText();
    }
    clickButton() {
        let elem = element.all(by.css('.accordion')).first();
        let container = element.all(by.css('.todo')).first();
        container.click();
    }

    todoStatusClickAndGet() {
        let elem = element.all(by.css('.accordion')).first();
        let container = element.all(by.css('.todo')).first();
        container.click();
        return element.all(by.css('.todoBodyStatus')).first().getText();
    }


    typeABody(body: string) {
        let input = element(by.id('todoBody'));
        input.click();
        input.sendKeys(body);
    }

//
//     // clickExpand() {
//     //     let input = element(by.id('todoBody'));
//     //     input.click();
//     //     input.sendKeys(body);
//     // }
//
//     clickAndSubmitOwner() {
//         let selectButton = element(by.id('submit'));
//         selectButton.click();
//     }
//
//     clickAddTodoButton(): promise.Promise<void> {
//         this.highlightElement(by.id('addNewTodo'));
//         return element(by.id('addNewTodo')).click();
//     }
//
    getTodos() {
        return element.all(by.className('todos'));
    }

    selectUpKey() {
        browser.actions().sendKeys(Key.ARROW_UP).perform();
    }

    selectDownKey() {
        browser.actions().sendKeys(Key.ARROW_DOWN).perform();
    }

    enter() {
        browser.actions().sendKeys(Key.ENTER).perform();
    }

    backspace() {
        browser.actions().sendKeys(Key.BACK_SPACE).perform();
    }


    getTodoByCategory() {
        let input = element(by.id('todoCategory'));
        input.click();
    }

    clickInput() {
        element(by.css('mat-radio-inner-circle')).click();
    }

    getUniqueTodo(_id: string) {
        let todo = element(by.id(_id)).getText();
        this.highlightElement(by.id(_id));

        return todo;
    }


    selectComplete() {
        let input = element(by.id('complete'));
        input.click();
    }

    selectSubmit() {
        let input = element(by.id('sub'));
        input.click();
    }

    selectInComplete() {
        let input = element(by.id('incomplete'));
        input.click();
    }

    selectboth() {
        let input = element(by.id('both'));
        input.click();
//     }
    }
    getOwner(owner:string){
        let input = element(by.id('todoOwner'));
        input.click();
        input.sendKeys(owner);
        let selectButton = element(by.id('submit'));
        selectButton.click();
        let elem = element.all(by.css('.accordion')).first();
        let container = element.all(by.css('.todo')).first();
        container.click();
    }
}
