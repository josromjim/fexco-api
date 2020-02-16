package jrj.fexco.business.model;

import java.util.Date;

/**
 * Business model POJO class that represents a ToDo task entity.
 * <p>
 * Note: As we use Hibernate in the current architecture, this class will be
 * mapped via "todo.hbm.xml" file to keep it totally independent and decoupled
 * of such framework. So we can ensure that it belongs exclusively to Business
 * model scope, granting its reusability.
 * </p>
 * 
 * @author José Romero
 *
 */
public class Todo {

	private int id;

	private Date creationDate;
	
	private Date changeDate;

	private String description;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date date) {
		this.creationDate = date;
	}


	public Date getChangeDate() {
		return changeDate;
	}

	public void setChangeDate(Date changeDate) {
		this.changeDate = changeDate;
	}
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
