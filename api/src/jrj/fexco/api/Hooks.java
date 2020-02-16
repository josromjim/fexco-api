package jrj.fexco.api;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Custom implementation of observer pattern based on the "hook" concept. Here the
 * observable are events identified by a string and the observers can subscribe
 * to such events without knowing the object that raises the event.
 * 
 * <p>
 * This class is a singleton to hold all hooks for the application.
 * </p>
 * 
 * @author José Romero
 *
 */
public final class Hooks {

	private static Hooks instance;

	/**
	 * This attribute stores the hooks and theirs observers' callbacks. 
	 */
	private Hashtable<String, List<HookCallback>> hooksTable;

	private Hooks() {
		this.hooksTable = new Hashtable<String, List<HookCallback>>();
	}

	/**
	 * Singleton initialization.
	 * 
	 * @return The hook instance.
	 */
	private synchronized static Hooks getInstance() {
		if (instance == null) {
			instance = new Hooks();
		}

		return instance;
	}

	/**
	 * Subscription must be synchronized.
	 * 
	 * @param hook Event name.
	 * @param callback Observer which handles the event.
	 */
	private synchronized void doSubscribe(String hook, HookCallback callback) {

		List<HookCallback> list = hooksTable.getOrDefault(hook, new ArrayList<HookCallback>());

		list.add(callback);

		hooksTable.put(hook, list);
	}

	/**
	 * Subscription of an observer to an event.
	 * 
	 * @param hook Event name.
	 * @param callback Observer which handles the event.
	 */
	public static void subscribe(String hook, HookCallback callback) {

		if (hook == null || hook.isEmpty())
			throw new InvalidParameterException("Parameter 'hook' must contain a valid string");
		if (callback == null)
			throw new InvalidParameterException("Parameter 'listener' must contain a value");

		getInstance().doSubscribe(hook, callback);
	}

	/**
	 * Notification of an event rising to observers.
	 * 
	 * @param hook Event name.
	 * @param params Parameters passed to the observer from the object which raises the event.
	 */
	public static void notify(String hook, Object params) {

		if (hook == null || hook.isEmpty())
			throw new InvalidParameterException("Parameter 'hook' must contain a valid string");

		List<HookCallback> list = getInstance().hooksTable.getOrDefault(hook, new ArrayList<HookCallback>());

		for (HookCallback callback : list) {
			// We let the lister to throw exceptions
			callback.execute(params);
		}
	}

}
