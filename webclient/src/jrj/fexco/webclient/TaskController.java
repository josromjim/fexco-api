package jrj.fexco.webclient;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jrj.fexco.webclient.api.rest.TodoApiModel;
import jrj.fexco.webclient.api.rest.TodoRestApiConsumer;

/**
 * Controller to be consumed by our AngularJS view. This class will call the
 * remote API using a @see TodoRestApiConsumer.
 * 
 * <p>
 * Note: All actions will return a @see ResponseEntity object to make easier the
 * handling of data and errors in the AngularJS view.
 * </p>
 * 
 * @author José Romero
 *
 */
@RestController
public class TaskController {

	/**
	 * The API consumed loaded by DI
	 */
	@Autowired
	private TodoRestApiConsumer consumer;

	@GetMapping(value = "/tasks", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<TodoApiModel>> getAllTasks() {
		ResponseEntity<List<TodoApiModel>> response = null;

		try {
			List<TodoApiModel> list = consumer.getAll();
			response = ResponseEntity.ok(list);
		} catch (Exception e) {
			// Log exception
			e.printStackTrace();
			response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}

		return response;
	}

	@GetMapping(value = "/tasks/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TodoApiModel> getTaskById(@PathVariable("id") int id) {
		ResponseEntity<TodoApiModel> response = null;

		try {
			TodoApiModel todo = consumer.getById(id);
			if (todo != null)
				response = ResponseEntity.ok(todo);
			else
				response = ResponseEntity.notFound().build();
		} catch (Exception e) {
			// Log exception
			e.printStackTrace();
			response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}

		return response;
	}

	@PostMapping(value = "/tasks", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TodoApiModel> createTask(@RequestBody TodoApiModel task) {
		ResponseEntity<TodoApiModel> response = null;

		try {
			TodoApiModel todo = consumer.add(task);
			response = ResponseEntity.ok(todo);
		} catch (Exception e) {
			// Log exception
			e.printStackTrace();
			response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}

		return response;
	}

	@PutMapping(value = "/tasks", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> updateTask(@RequestBody TodoApiModel task) {
		ResponseEntity<?> response = null;

		try {
			if (consumer.update(task))
				response = ResponseEntity.ok().build();
			else
				response = ResponseEntity.notFound().build();
		} catch (Exception e) {
			// Log exception
			e.printStackTrace();
			response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}

		return response;
	}

	@DeleteMapping(value = "/tasks/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TodoApiModel> deleteTask(@PathVariable("id") int id) {
		ResponseEntity<TodoApiModel> response = null;

		try {
			TodoApiModel deleted = consumer.delete(id);
			if (deleted != null)
				response = ResponseEntity.ok(deleted);
			else
				response = ResponseEntity.notFound().build();
		} catch (Exception e) {
			// Log exception
			e.printStackTrace();
			response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}

		return response;
	}
}
