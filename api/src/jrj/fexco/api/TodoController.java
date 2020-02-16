package jrj.fexco.api;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.MediaType;

import jrj.fexco.api.security.Secure;
import jrj.fexco.api.viewmodel.TodoViewModel;
import jrj.fexco.business.model.Todo;
import jrj.fexco.business.repository.ITodoRepository;

/**
 * ToDo list main Controller. This class manages API requests and provides
 * actions to operate over the model.
 * <p>
 * The class will be loaded by jersey servlet container when route "todo" is
 * reached.
 * </p>
 * 
 * @author José Romero
 *
 */
@Secure
@Path("/todo")
public class TodoController {

	/**
	 * Repository to manage business logic and model handling.
	 * <p>
	 * Note: The implementation will be injected following DI pattern to provide
	 * IoC. This way we would be able to change the implementation on tests.
	 * </p>
	 */
	@Inject
	ITodoRepository repository;

	/**
	 * Gets all ToDos
	 * 
	 * @return A list of ToDos stored
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public List<TodoViewModel> get() {
		try {
			List<Todo> all = repository.listTodos();

			List<TodoViewModel> output = new ArrayList<TodoViewModel>();

			for (Todo model : all) {
				output.add(Mapper.modelToViewModel(model));
			}

			return output;
		} catch (Exception e) { // We could differentiate exceptions
			// Log exception
			e.printStackTrace();
			throw new ServerErrorException(500); // and apply an specific code for each exception
		}
	}

	/**
	 * Get a ToDo by Id
	 * 
	 * @param id Given Id to search the ToDo
	 * @return The ToDo if found
	 * @throws NotFoundException if no ToDo found
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{id}")
	public TodoViewModel getTodo(@PathParam("id") int id) {
		Todo model = null;

		try {
			model = repository.getTodoById(id);
		} catch (Exception e) { // We could differentiate exceptions
			// Log exception
			e.printStackTrace();
			throw new ServerErrorException(500); // and apply an specific code for each exception
		}

		if (model != null) {
			TodoViewModel viewmodel = Mapper.modelToViewModel(model);
			return viewmodel;
		} else {
			throw new NotFoundException(); // 404 if not found
		}
	}

	/**
	 * Insert a ToDo
	 * 
	 * @param newTask New ToDo to insert
	 * @return The inserted ToDo
	 */
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public TodoViewModel post(TodoViewModel newTask) {
		try {
			Todo todo = new Todo();

			todo.setDescription(newTask.getTask());
			int id = repository.addTodo(todo);
			todo.setId(id);

			TodoViewModel viewmodel = Mapper.modelToViewModel(todo);

			Hooks.notify("add", viewmodel);

			return viewmodel;
		} catch (Exception e) { // We could differentiate exceptions
			// Log exception
			e.printStackTrace();
			throw new ServerErrorException(500); // and apply an specific code for each exception
		}
	}

	/**
	 * Updates a ToDo
	 * 
	 * @param updatedTask ToDo to update
	 * @return The updated ToDo
	 * @throws NotFoundException if no ToDo found
	 */
	@PUT
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public TodoViewModel put(TodoViewModel updatedTask) {
		try {
			Todo todo = Mapper.viewModelToModel(updatedTask);

			if (repository.updateTodo(todo)) {

				Hooks.notify("update", updatedTask);

				return updatedTask;
			} else {
				throw new NotFoundException(); // 404 if not found
			}
		} catch (NotFoundException nfe) {
			throw nfe; // We bubble up if NotFoundException
		} catch (Exception e) { // We could differentiate exceptions
			// Log exception
			e.printStackTrace();
			throw new ServerErrorException(500); // and apply an specific code for each exception
		}
	}

	/**
	 * Deletes a ToDo
	 * 
	 * @param id Given ToDo id to delete
	 * @return The deleted ToDo
	 * @throws NotFoundException if no ToDo found
	 */
	@DELETE
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{id}")
	public TodoViewModel delete(@PathParam("id") int id) {
		try {
			Todo model = repository.removeTodo(id);

			if (model != null) {
				TodoViewModel viewmodel = Mapper.modelToViewModel(model);

				Hooks.notify("remove", viewmodel);

				return viewmodel;

			} else {
				throw new NotFoundException(); // 404 if not found
			}
		} catch (NotFoundException nfe) {
			throw nfe; // We bubble up if NotFoundException
		} catch (Exception e) { // We could differentiate exceptions
			// Log exception
			e.printStackTrace();
			throw new ServerErrorException(500); // and apply an specific code for each exception
		}
	}
}
