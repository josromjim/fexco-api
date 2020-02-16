package jrj.fexco.business.repository;

import java.util.List;
import jrj.fexco.business.model.Todo;

/**
 * Repository interface for ToDo List domain. It exposes all operations we can make over the ToDo model.
 * 
 * @author José Romero
 *
 */
public interface ITodoRepository {
	public int addTodo(Todo todo);

	public boolean updateTodo(Todo todo);

	public Todo removeTodo(int id);

	public Todo getTodoById(int id);

	public List<Todo> listTodos();
}
