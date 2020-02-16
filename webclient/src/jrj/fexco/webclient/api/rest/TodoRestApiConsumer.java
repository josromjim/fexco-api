package jrj.fexco.webclient.api.rest;

import java.util.List;

/**
 * Interface for the API consumer.
 * 
 * @author José Romero
 *
 */
public interface TodoRestApiConsumer {
	
	public TodoApiModel getById(int id);
	public List<TodoApiModel> getAll();
	public TodoApiModel add(TodoApiModel todo);
	public boolean update(TodoApiModel todo);
	public TodoApiModel delete(int id);

}
