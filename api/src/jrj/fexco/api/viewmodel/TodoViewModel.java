package jrj.fexco.api.viewmodel;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * View Model class for ToDo list tasks. This class will be used only on views
 * as transfer object between API consumer and API controller. We can, for
 * example, annotate it for view purposes without affecting the model.
 * 
 * @author José Romero
 *
 */
@XmlRootElement
public class TodoViewModel {
	private int id;
	private String task;

	public TodoViewModel() {
	}

	public TodoViewModel(int id, String task) {
		this.id = id;
		this.task = task;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTask() {
		return task;
	}

	public void setTask(String task) {
		this.task = task;
	}

}
