package jrj.fexco.business.repository.hibernate;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import jrj.fexco.business.model.Todo;
import jrj.fexco.business.repository.ITodoRepository;

/**
 * Implementation of IRepository to manage all ToDo domain operations and using
 * Hibernate as DAO and ORM.
 * 
 * @author José Romero
 *
 */
public class TodoRepository implements ITodoRepository {

	private SessionFactory sessionFactory;

	public TodoRepository() {
		// Here we could create an utility to get the SessionFactory as a Singleton
		// but that's not necessary in this case because it is used and created by a
		// Singleton class.
		try {
			this.sessionFactory = new Configuration().configure().buildSessionFactory();
		} catch (Throwable ex) {
			System.err.println("Initial SessionFactory creation failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	@Override
	public int addTodo(Todo todo) {
		Session session = this.sessionFactory.openSession();
		session.beginTransaction();
		// Set current date as creation date and update date
		Date now = new Date();
		todo.setCreationDate(now);
		todo.setChangeDate(now);
		session.save(todo);
		session.getTransaction().commit();
		session.close();
		return todo.getId();
	}

	@Override
	public boolean updateTodo(Todo todo) {
		Session session = this.sessionFactory.openSession();
		Todo updatingTodo = session.find(Todo.class, new Integer(todo.getId()));
		if (updatingTodo != null) {
			session.beginTransaction();
			// Here we merge data, in this case it's simple and we do not need an extra
			// utility
			updatingTodo.setChangeDate(new Date());
			updatingTodo.setDescription(todo.getDescription());
			// Persist
			session.update(updatingTodo);
			session.getTransaction().commit();
		}
		session.close();

		return updatingTodo != null;
	}

	@Override
	public Todo removeTodo(int id) {
		Session session = this.sessionFactory.openSession();
		Todo todo = (Todo) session.get(Todo.class, new Integer(id));

		if (todo != null) {
			session.beginTransaction();
			session.delete(todo);
			session.getTransaction().commit();
		}

		session.close();

		return todo;
	}

	@Override
	public Todo getTodoById(int id) {
		Session session = this.sessionFactory.openSession();
		Todo todo = (Todo) session.get(Todo.class, new Integer(id));
		session.close();

		return todo;
	}

	@Override
	public List<Todo> listTodos() {
		// We assume the dataset won't be big enough to cause problems
		Session session = this.sessionFactory.openSession();
		@SuppressWarnings("unchecked")
		List<Todo> tasks = session.createQuery("FROM Todo").list();
		session.close();

		return tasks;
	}

}
