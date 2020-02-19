package jrj.fexco.test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.RegularExpressionValueMatcher;
import org.skyscreamer.jsonassert.comparator.CustomComparator;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

public class StepDefinitions {

	private static final String BASE_API = "http://localhost:8080/api/api/todo/";
	private static final String AUTH_TOKEN = "YXV0aGVudGljYXRpb250b2tlbg==";
	private static final String APPLICATION_JSON = "application/json";

	private static final String TEXT_NEW_TASK = "Cucumber test todo";
	private static final String TEXT_UPDATED_TASK = "Cucumber test todo updated";

	private final CloseableHttpClient httpClient = HttpClients.createDefault();

	private HttpResponse response;
	private static String idCreated; // We will operate with created todo in first test

	/*
	 * Create
	 */

	@Given("The consumer sends a new todo")
	public void the_consumer_sends_a_new_todo() throws ClientProtocolException, IOException {
		HttpPost request = new HttpPost(BASE_API);
		StringEntity entity = new StringEntity("{\"task\" : \"" + TEXT_NEW_TASK + "\"}");
		request.addHeader("content-type", APPLICATION_JSON);
		request.addHeader("authorization", "Bearer " + AUTH_TOKEN);
		request.setEntity(entity);
		response = httpClient.execute(request);
	}

	@Then("The system adds the todo to the database")
	public void the_system_adds_the_todo_to_the_database() {
		// TODO Here we could check the database
	}

	@Then("sends a message with data created to IoT Hub to deliver it through MQTT")
	public void sends_a_message_with_data_created_to_IoT_Hub_to_deliver_it_through_MQTT() {
		// TODO Here we can implement a IoT Receiver and check if the message
	}

	@Then("returns the data created")
	public void returns_the_data_created() throws Exception {
		String responseStr = this.responseToString(this.response);

		assertEquals(200, this.response.getStatusLine().getStatusCode());
		JSONAssert.assertEquals("{\"task\" : \"" + TEXT_NEW_TASK + "\"}", responseStr, JSONCompareMode.LENIENT);
		assertThat(responseStr, containsString("\"id\":"));
		JSONObject o = new JSONObject(responseStr);
		idCreated = o.getString("id");
	}

	@Given("The consumer sends a new todo with wrong data")
	public void the_consumer_sends_a_new_todo_with_wrong_data() throws ClientProtocolException, IOException {
		HttpPost request = new HttpPost(BASE_API);
		StringEntity entity = new StringEntity("{\"id\" : \"wrongdata\", \"task\" : \"" + TEXT_NEW_TASK + "\"}");
		request.addHeader("content-type", APPLICATION_JSON);
		request.addHeader("authorization", "Bearer " + AUTH_TOKEN);
		request.setEntity(entity);
		response = httpClient.execute(request);
	}

	@Then("The system returns an error")
	public void the_system_returns_an_error() {
		assertEquals(500, this.response.getStatusLine().getStatusCode());
	}

	/*
	 * Get
	 */

	@Given("The consumer sends a get request for Todo id")
	public void the_consumer_sends_a_get_request_for_Todo_id() throws ClientProtocolException, IOException {
		HttpGet request = new HttpGet(BASE_API + idCreated);
		request.addHeader("content-type", APPLICATION_JSON);
		request.addHeader("authorization", "Bearer " + AUTH_TOKEN);
		response = httpClient.execute(request);
	}

	@Then("The system gets the todo from the database")
	public void the_system_gets_the_todo_from_the_database() {
		// Nothing to check here
	}

	@Then("returns the todo obtained")
	public void returns_the_todo_obtained() throws JSONException, ParseException, IOException {
		String responseStr = this.responseToString(this.response);

		assertEquals(200, this.response.getStatusLine().getStatusCode());
		JSONAssert.assertEquals("{\"id\" : " + idCreated + ", \"task\" : \"" + TEXT_NEW_TASK + "\"}", responseStr,
				JSONCompareMode.LENIENT);
	}

	@Given("The consumer requests get with non-existent todo id")
	public void the_consumer_sends_a_new_todo_with_wrong_id() throws ClientProtocolException, IOException {
		HttpGet request = new HttpGet(BASE_API + "0");
		request.addHeader("content-type", APPLICATION_JSON);
		request.addHeader("authorization", "Bearer " + AUTH_TOKEN);
		response = httpClient.execute(request);
	}

	@Then("The system returns Not Found")
	public void the_system_returns_Not_Found() {
		assertEquals(404, this.response.getStatusLine().getStatusCode());
	}

	@Given("The consumer sends a get all request")
	public void the_consumer_sends_a_get_all_request() throws ClientProtocolException, IOException {
		HttpGet request = new HttpGet(BASE_API);
		request.addHeader("content-type", APPLICATION_JSON);
		request.addHeader("authorization", "Bearer " + AUTH_TOKEN);
		response = httpClient.execute(request);
	}

	@Then("The system gets all todos from the database")
	public void the_system_gets_all_todos_from_the_database() {
		// Nothig to check here
	}

	@Then("returns the data list obtained")
	public void returns_the_data_list_obtained() throws ParseException, IOException, JSONException {
		String responseStr = this.responseToString(this.response);

		JSONArray array = new JSONArray(responseStr);

		assertTrue(array.length() > 0);

		JSONAssert.assertEquals("{id:x}", array.get(0).toString(), new CustomComparator(JSONCompareMode.LENIENT,
				new Customization("id", new RegularExpressionValueMatcher<Object>("\\d"))));
	}

	/*
	 * Update
	 */

	@Given("The consumer sends a todo to update")
	public void the_consumer_sends_a_todo_to_update() throws ClientProtocolException, IOException {
		HttpPut request = new HttpPut(BASE_API);
		StringEntity entity = new StringEntity(
				"{\"id\" : " + idCreated + ", \"task\" : \"" + TEXT_UPDATED_TASK + "\"}");
		request.addHeader("content-type", APPLICATION_JSON);
		request.addHeader("authorization", "Bearer " + AUTH_TOKEN);
		request.setEntity(entity);
		response = httpClient.execute(request);
	}

	@Then("The system updates the todo to the database")
	public void the_system_updates_the_todo_to_the_database() {
		// TODO Here we could check the database
	}

	@Then("sends a message with data updated to IoT Hub to deliver it through MQTT")
	public void sends_a_message_with_data_updated_to_IoT_Hub_to_deliver_it_through_MQTT() {
		// TODO Here we can implement a IoT Receiver and check if the message
	}

	@Then("returns the data updated")
	public void returns_the_data_updated() throws ParseException, IOException, JSONException {
		String responseStr = this.responseToString(this.response);

		assertEquals(200, this.response.getStatusLine().getStatusCode());
		JSONAssert.assertEquals("{\"id\" : " + idCreated + ", \"task\" : \"" + TEXT_UPDATED_TASK + "\"}", responseStr,
				JSONCompareMode.LENIENT);
	}

	@Given("The consumer sends a updating todo with non-existent todo")
	public void the_consumer_sends_a_updating_todo_with_non_existent_todo()
			throws ClientProtocolException, IOException {
		HttpPut request = new HttpPut(BASE_API);
		StringEntity entity = new StringEntity("{\"id\" : 0, \"task\" : \"" + TEXT_UPDATED_TASK + "\"}");
		request.addHeader("content-type", APPLICATION_JSON);
		request.addHeader("authorization", "Bearer " + AUTH_TOKEN);
		request.setEntity(entity);
		response = httpClient.execute(request);
	}

	@Given("The consumer sends a updating todo with wrong data")
	public void the_consumer_sends_a_updating_todo_with_wrong_data() throws ClientProtocolException, IOException {
		HttpPut request = new HttpPut(BASE_API);
		StringEntity entity = new StringEntity("{\"id\" : \"wrongdata\", \"task\" : \"" + TEXT_UPDATED_TASK + "\"}");
		request.addHeader("content-type", APPLICATION_JSON);
		request.addHeader("authorization", "Bearer " + AUTH_TOKEN);
		request.setEntity(entity);
		response = httpClient.execute(request);
	}

	/*
	 * Delete
	 */

	@Given("The consumer sends a delete request for Todo id")
	public void the_consumer_sends_a_delete_request_for_Todo_id() throws ClientProtocolException, IOException {
		HttpDelete request = new HttpDelete(BASE_API + idCreated);
		request.addHeader("content-type", APPLICATION_JSON);
		request.addHeader("authorization", "Bearer " + AUTH_TOKEN);
		response = httpClient.execute(request);
	}

	@Then("The system deletes the todo from the database")
	public void the_system_deletes_the_todo_from_the_database() {
		// TODO Here we can implement a IoT Receiver and check if the message
	}

	@Then("sends a message with data deleted to IoT Hub to deliver it through MQTT")
	public void sends_a_message_with_data_deleted_to_IoT_Hub_to_deliver_it_through_MQTT() {
		// TODO Here we can implement a IoT Receiver and check if the message
	}

	@Then("returns the data deleted")
	public void returns_the_data_deleted() throws ParseException, IOException, JSONException {
		String responseStr = this.responseToString(this.response);

		assertEquals(200, this.response.getStatusLine().getStatusCode());
		JSONAssert.assertEquals("{\"id\" : " + idCreated + ", \"task\" : \"" + TEXT_UPDATED_TASK + "\"}", responseStr,
				JSONCompareMode.LENIENT);
	}

	@Given("The consumer sends a delete todo with non-existent todo")
	public void the_consumer_sends_a_delete_todo_with_non_existent_todo() throws ClientProtocolException, IOException {
		HttpDelete request = new HttpDelete(BASE_API + "0");
		request.addHeader("content-type", APPLICATION_JSON);
		request.addHeader("authorization", "Bearer " + AUTH_TOKEN);
		response = httpClient.execute(request);
	}

	private String responseToString(HttpResponse response) throws ParseException, IOException {
		HttpEntity entity = response.getEntity();
		Header encoding = entity.getContentEncoding();
		String encodingStr = encoding != null && encoding.getValue() != null ? encoding.getValue() : "UTF-8";
		String body = EntityUtils.toString(entity, encodingStr);
		return body;
	}
}
