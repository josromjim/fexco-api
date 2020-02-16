package jrj.fexco.api;

import jrj.fexco.api.viewmodel.TodoViewModel;
import jrj.fexco.business.model.Todo;

/**
 * This class allows to map Models to ViewModels and vice versa.
 * 
 * @author José Romero
 *
 */
public class Mapper {

	public static TodoViewModel modelToViewModel(Todo model) {
		return new TodoViewModel(model.getId(), model.getDescription());
	}

	public static Todo viewModelToModel(TodoViewModel viewmodel) {
		Todo model = new Todo();
		model.setId(viewmodel.getId());
		model.setDescription(viewmodel.getTask());

		return model;
	}
}
