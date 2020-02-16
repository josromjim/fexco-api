package jrj.fexco.webclient.api.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * This is the Api consumer implementation made using Spring rest consumer.
 * 
 * @author Jos� Romero
 *
 */
@Component
public class TodoSpringApiConsumer implements TodoRestApiConsumer {

	@Autowired
	private RestTemplate restTemplate;

	/**
	 * URL to connect the API. This URL could be obtained from a configuration file
	 * instead of hardcoded.
	 */
	private static final String API_BASE_URL = "http://localhost:8080/api/api/todo/";

	/**
	 * Initialize the injection of authorization header.
	 */
	@PostConstruct
	private void init() {
		List<ClientHttpRequestInterceptor> interceptors = new ArrayList<ClientHttpRequestInterceptor>();
		interceptors.add(new SecurityInterceptor());
		restTemplate.setInterceptors(interceptors);
	}

	@Override
	public TodoApiModel getById(int id) {
		TodoApiModel todo = null;

		try {
			todo = restTemplate.getForObject(API_BASE_URL + id, TodoApiModel.class);
		} catch (HttpClientErrorException ex) {
			// If not found, we just return null. Otherwise we rise the exception.
			if (ex.getStatusCode() != HttpStatus.NOT_FOUND) {
				throw ex;
			}
		}

		return todo;
	}

	@Override
	public List<TodoApiModel> getAll() {
		List<TodoApiModel> list = new ArrayList<TodoApiModel>();

		try {
			TodoApiModel[] response = restTemplate.getForObject(API_BASE_URL, TodoApiModel[].class);
			for (TodoApiModel todo : response) {
				list.add(todo);
			}
		} catch (HttpClientErrorException ex) {
			if (ex.getStatusCode() != HttpStatus.NOT_FOUND) {
				throw ex;
			}
		}

		return list;
	}

	@Override
	public TodoApiModel add(TodoApiModel todo) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<TodoApiModel> request = new HttpEntity<TodoApiModel>(todo, headers);
			todo = restTemplate.postForObject(API_BASE_URL, request, TodoApiModel.class);
		} catch (HttpClientErrorException ex) {
			if (ex.getStatusCode() != HttpStatus.NOT_FOUND) {
				throw ex;
			}
		}

		return todo;
	}

	@Override
	public boolean update(TodoApiModel todo) {
		boolean updated = false;

		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<TodoApiModel> request = new HttpEntity<TodoApiModel>(todo, headers);
			restTemplate.put(API_BASE_URL, request);
			updated = true;
		} catch (HttpClientErrorException ex) {
			if (ex.getStatusCode() != HttpStatus.NOT_FOUND) {
				throw ex;
			}
		}

		return updated;
	}

	@Override
	public TodoApiModel delete(int id) {
		TodoApiModel todo = null;

		try {
			ResponseEntity<TodoApiModel> response = restTemplate.exchange(API_BASE_URL + id, HttpMethod.DELETE, null,
					TodoApiModel.class);
			todo = response.getBody();
		} catch (HttpClientErrorException ex) {
			if (ex.getStatusCode() != HttpStatus.NOT_FOUND) {
				throw ex;
			}
		}

		return todo;
	}

	/**
	 * This class will control and inject the security header for the API .
	 * 
	 * @author Jos� Romero
	 *
	 */
	private class SecurityInterceptor implements ClientHttpRequestInterceptor {

		public SecurityInterceptor() {
		}

		@Override
		public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
				throws IOException {
			// As we said before, this is just an example. We should use a unique and
			// expiring token generated by the API server.
			request.getHeaders().set("Authorization", "Bearer YXV0aGVudGljYXRpb250b2tlbg==");
			return execution.execute(request, body);
		}
	}

}
