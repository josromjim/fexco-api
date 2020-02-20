package jrj.fexco.test.webclient;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Steps definitions for WebClient Tests.
 * <p>
 * This class uses Selenium to test all UI functionality.
 * </p>
 * 
 * @author José Romero
 *
 */
public class StepDefinitions {

	private static final String BASE_URL = "http://localhost:8080/webclient/";
	private static final String TEXT_NEW_TASK = "Cucumber test UI";
	private static final String TEXT_UPDATED_TASK = "Cucumber test UI updated";

	private final WebDriver driver;
	private WebDriver otherDriver; // Second driver for IoT

	public StepDefinitions() {
		driver = new ChromeDriver(getChromeOptions());
	}

	/**
	 * To solve problem: Timed out receiving message from renderer
	 * 
	 * @return options
	 */
	private ChromeOptions getChromeOptions() {
		ChromeOptions options = new ChromeOptions();
		options.addArguments("enable-automation");
		options.addArguments("--headless");
		options.addArguments("--window-size=1920,1080");
		options.addArguments("--no-sandbox");
		options.addArguments("--disable-extensions");
		options.addArguments("--dns-prefetch-disable");
		options.addArguments("--disable-gpu");
		options.setPageLoadStrategy(PageLoadStrategy.NORMAL);

		return options;
	}

	private void waitAngularLoads() {
		new WebDriverWait(driver, 10L).until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {

				JavascriptExecutor js = (JavascriptExecutor) d;

				Boolean isUndefined = (Boolean) js
						.executeScript("return angular.element(document).injector() === undefined");

				return !isUndefined;
			}
		});
	}

	private void waitAngularRequest() {
		new WebDriverWait(driver, 10L).until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {

				JavascriptExecutor js = (JavascriptExecutor) d;

				Boolean isRequesting = (Boolean) js.executeScript(
						"return angular.element(document).injector().get('$http').pendingRequests.length === 0");

				return isRequesting;
			}
		});
	}

	@Given("The user opens the main site")
	public void the_user_opens_the_main_site() {
		driver.get(BASE_URL);

		waitAngularRequest();
	}

	@Then("The system calls the API to load all tasks")
	public void the_system_calls_the_API_to_load_all_tasks() throws InterruptedException {
		// Wait for 3 seconds to get all loaded
		new WebDriverWait(driver, 3L).until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				return d.findElement(By.tagName("table")).isDisplayed();
			}
		});
	}

	@Then("shows all tasks")
	public void shows_all_tasks() {
		assertTrue(driver.findElement(By.tagName("table")).isDisplayed()
				&& !driver.findElement(By.id("no-todos")).isDisplayed());
	}

	@When("There are no tasks on the system")
	public void there_are_no_tasks_on_the_system() throws InterruptedException {
		JavascriptExecutor js = (JavascriptExecutor) driver;

		waitAngularRequest();

		// Inject a mock empty list
		js.executeScript(
				"angular.element(document.getElementById('no-todos')).scope().todoList.todos = [];angular.element(document.getElementById('no-todos')).scope().$apply()");
	}

	@Then("shows a message notifying there are no tasks")
	public void shows_a_message_notifying_there_are_no_tasks() {
		assertTrue(!driver.findElement(By.tagName("table")).isDisplayed()
				&& driver.findElement(By.id("no-todos")).isDisplayed());
	}

	@Given("The user writes a task in the field")
	public void the_user_writes_a_task_in_the_field() {
		driver.get(BASE_URL);

		waitAngularLoads();
		WebElement element = driver.findElement(By.id("task-input"));
		// Enter text
		element.clear();
		element.sendKeys(TEXT_NEW_TASK);
	}

	@When("the user submits the task clicking")
	public void the_user_submits_the_task_clicking() {
		WebElement addBtn = driver.findElement(By.id("add-btn"));
		// Enter text
		addBtn.click();
	}

	@Then("The system calls the API to store the task")
	public void the_system_calls_the_API_to_store_the_task() throws InterruptedException {
		// Wait for API calling
		waitAngularRequest();
	}

	@Then("adds the task created to the list")
	public void adds_the_task_created_to_the_list() {
		JavascriptExecutor js = (JavascriptExecutor) driver;

		Map<?, ?> lastElement = (Map<?, ?>) js.executeScript(
				"return angular.element(document.getElementById('no-todos')).scope().todoList.todos[angular.element(document.getElementById('no-todos')).scope().todoList.todos.length-1]");
		assertThat(lastElement.get("task").toString(), containsString(TEXT_NEW_TASK));
	}

	@Given("The user does not write anything in the field")
	public void the_user_does_not_write_anything_in_the_field() {
		driver.get(BASE_URL);

		waitAngularLoads();
		WebElement element = driver.findElement(By.id("task-input"));
		// Enter text
		element.clear();
	}

	@Given("the user does not write anything in the field")
	public void and_the_user_does_not_write_anything_in_the_field() {
		WebElement element = driver.findElement(By.id("task-input"));
		// Enter text
		element.clear();
	}

	@Given("the field is empty")
	public void the_field_is_empty() {
		WebElement element = driver.findElement(By.id("task-input"));
		// Enter text
		element.clear();
	}

	@When("the user submits the field")
	public void the_user_submits_the_field() {
		WebElement form = driver.findElement(By.id("todo-form"));

		form.submit();
	}

	@Then("the web notify the user to write the task before submitting")
	public void the_web_notify_the_user_to_write_the_task_before_submitting() {
		WebElement element = driver.findElement(By.id("task-input"));

		JavascriptExecutor js = (JavascriptExecutor) driver;
		boolean isRequired = (Boolean) js.executeScript("return arguments[0].required;", element);

		assertTrue(isRequired);
	}

	@Given("The user clicks on a task's {string} button")
	public void the_user_clicks_on_a_task_s_button(String button) throws InterruptedException {
		driver.get(BASE_URL);

		// Wait for loads
		waitAngularLoads();
		waitAngularRequest();

		WebElement element = null;
		switch (button) {
		case "Delete":
			List<WebElement> deletes = driver.findElements(By.className("btn-danger"));
			element = deletes.get(deletes.size() - 1);
			break;
		case "Update":
			List<WebElement> updates = driver.findElements(By.className("btn-warning"));
			element = updates.get(updates.size() - 1);
			break;

		default:
			break;
		}

		// Let's raise NullPointerException if not valid button
		element.click();
	}

	@When("the user edits the task and clicks on Save button")
	public void when_the_user_clicks_on_button() {
		WebElement saveBtn = driver.findElement(By.id("save-btn"));

		saveBtn.click();
	}

	@Then("The system calls the API to delete the task")
	public void the_system_calls_the_API_to_delete_the_task() throws InterruptedException {
		// Wait to get all loaded
		waitAngularRequest();
	}

	@Then("delete the task from the list")
	public void delete_the_task_from_the_list() {
		JavascriptExecutor js = (JavascriptExecutor) driver;

		Map<?, ?> lastElement = (Map<?, ?>) js.executeScript(
				"return angular.element(document.getElementById('no-todos')).scope().todoList.todos[angular.element(document.getElementById('no-todos')).scope().todoList.todos.length-1]");
		assertThat(lastElement.get("task").toString(), not(containsString(TEXT_UPDATED_TASK)));
	}

	@Given("The user clicks on {string} button")
	public void the_user_clicks_on_button(String button) {
		driver.get(BASE_URL);

		waitAngularLoads();
		WebElement element = null;
		switch (button) {
		case "Reset":
			element = driver.findElement(By.id("reset-btn"));
			break;
		case "Save":
			element = driver.findElement(By.id("save-btn"));
			break;

		default:
			break;
		}

		// Let's raise NullPointerException if not valid button
		element.click();
	}

	@Given("the UI changes to edit mode")
	public void the_UI_changes_to_mode() {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		boolean editMode = (Boolean) js.executeScript(
				"return angular.element(document.getElementById('no-todos')).scope().todoList.editMode;");

		assertTrue(editMode == true);

		WebElement element = driver.findElement(By.id("task-input"));
		// Enter text
		element.clear();
		element.sendKeys(TEXT_UPDATED_TASK);
	}

	@Then("The system calls the API to update the task")
	public void the_system_calls_the_API_to_update_the_task() throws InterruptedException {
		// Wait to get all loaded
		waitAngularRequest();
	}

	@Then("updates the task on the list")
	public void updates_the_task_on_the_list() {
		JavascriptExecutor js = (JavascriptExecutor) driver;

		Map<String, Object> lastElement = (Map<String, Object>) js.executeScript(
				"return angular.element(document.getElementById('no-todos')).scope().todoList.todos[angular.element(document.getElementById('no-todos')).scope().todoList.todos.length-1]");
		assertThat(lastElement.get("task").toString(), containsString(TEXT_UPDATED_TASK));
	}

	@Given("The UI is in edit mode")
	public void the_UI_is_in_mode() throws InterruptedException {
		driver.get(BASE_URL);

		// Wait loads
		waitAngularLoads();
		waitAngularRequest();

		// Click last Edit button
		List<WebElement> updates = driver.findElements(By.className("btn-warning"));
		WebElement element = updates.get(updates.size() - 1);

		element.click();
	}

	@Given("There is an error between server and view")
	public void there_is_an_error_between_server_and_view() throws InterruptedException {
		driver.get(BASE_URL);

		waitAngularLoads();
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript(
				"angular.element(document.getElementById('no-todos')).scope().todoList.error = true;angular.element(document.getElementById('no-todos')).scope().$apply()");
	}

	@Then("the UI shows a message detailing the error")
	public void the_UI_shows_a_message_detailing_the_error() {
		WebElement element = driver.findElement(By.id("alerts-panel"));

		assertTrue(element.isDisplayed());
	}

	@Given("The IoT module receives a {string} message with a task")
	public void the_IoT_module_receives_a_message_with_a_task(String type) {
		this.otherDriver = new ChromeDriver(getChromeOptions());
		driver.get(BASE_URL);
		this.otherDriver.get(BASE_URL);

		// Wait loads
		waitAngularLoads();
		waitAngularRequest();

		WebElement input = driver.findElement(By.id("task-input"));
		WebElement form = driver.findElement(By.id("todo-form"));

		switch (type) {
		case "insert":
			// Enter text
			input.clear();
			input.sendKeys(TEXT_NEW_TASK);
			form.submit();
			break;
		case "update":
			List<WebElement> updates = driver.findElements(By.className("btn-warning"));
			WebElement updateBtn = updates.get(updates.size() - 1);
			updateBtn.click();

			input.clear();
			input.sendKeys(TEXT_UPDATED_TASK);
			form.submit();
			break;
		case "delete":
			List<WebElement> deletes = driver.findElements(By.className("btn-danger"));
			WebElement deleteBtn = deletes.get(deletes.size() - 1);
			deleteBtn.click();
			break;

		default:
			break;
		}
	}

	@Then("The web adds the task to the list of the UI")
	public void the_web_adds_the_task_to_the_list_of_the_UI() {
		// Wait for 3 seconds
		boolean isAdded = true;
		try {
			new WebDriverWait(otherDriver, 3L).until(new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver d) {

					JavascriptExecutor js = (JavascriptExecutor) d;
					Map<String, Object> lastElement = (Map<String, Object>) js.executeScript(
							"return angular.element(document.getElementById('no-todos')).scope().todoList.todos[angular.element(document.getElementById('no-todos')).scope().todoList.todos.length-1]");

					return lastElement != null && lastElement.get("task").toString().contains(TEXT_NEW_TASK);
				}
			});
		} catch (TimeoutException e) {
			// Not found => fail
			isAdded = false;
		}

		assertTrue(isAdded);
	}

	@Then("The web updates the task on the list of the UI")
	public void the_web_updates_the_task_on_the_list_of_the_UI() {
		// Wait for 3 seconds
		boolean isUpdated = true;
		try {
			new WebDriverWait(otherDriver, 3L).until(new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver d) {

					JavascriptExecutor js = (JavascriptExecutor) d;
					Map<String, Object> lastElement = (Map<String, Object>) js.executeScript(
							"return angular.element(document.getElementById('no-todos')).scope().todoList.todos[angular.element(document.getElementById('no-todos')).scope().todoList.todos.length-1]");

					return lastElement != null && lastElement.get("task").toString().contains(TEXT_UPDATED_TASK);
				}
			});
		} catch (TimeoutException e) {
			// Not found => fail
			isUpdated = false;
		}

		assertTrue(isUpdated);
	}

	@Then("The web removes the task from the list of the UI")
	public void the_web_removes_the_task_from_the_list_of_the_UI() {
		// Wait for 3 seconds
		boolean idDeleted = true;
		try {
			new WebDriverWait(otherDriver, 3L).until(new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver d) {

					JavascriptExecutor js = (JavascriptExecutor) d;
					Map<String, Object> lastElement = (Map<String, Object>) js.executeScript(
							"return angular.element(document.getElementById('no-todos')).scope().todoList.todos[angular.element(document.getElementById('no-todos')).scope().todoList.todos.length-1]");

					return lastElement != null && !lastElement.get("task").toString().contains(TEXT_UPDATED_TASK);
				}
			});
		} catch (TimeoutException e) {
			// Not found => deleted
			idDeleted = false;
		}

		assertTrue(!idDeleted);
	}

	@Then("the task field is set to empty")
	public void the_task_field_is_set_to_empty() {
		WebElement element = driver.findElement(By.id("task-input"));

		assertTrue(element.getText().equals(""));
	}

	@Then("editing mode is disabled")
	public void editing_mode_is_disabled() {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		boolean editMode = (Boolean) js.executeScript(
				"return angular.element(document.getElementById('no-todos')).scope().todoList.editMode;");

		assertTrue(editMode == false);
	}

	@After()
	public void closeBrowser() {
		driver.quit();
		if (otherDriver != null)
			otherDriver.quit();
	}
}
