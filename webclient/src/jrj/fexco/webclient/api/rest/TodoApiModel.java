package jrj.fexco.webclient.api.rest;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * View Model class for ToDo list tasks from API. We will also use this model
 * from the API into the view.
 * 
 * @author José Romero
 *
 */
@XmlRootElement
public class TodoApiModel {
	private int id;
	private String task;

	public TodoApiModel() {
	}

	public TodoApiModel(int id, String task) {
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
