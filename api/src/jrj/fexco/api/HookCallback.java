package jrj.fexco.api;

/**
 * Abstract class that defines the methods for a hook callback class.
 * 
 * @author José Romero
 *
 */
public abstract class HookCallback {

	public abstract void execute(Object params);

	/*
	 * Note: We declare this class as abstract instead to create an interface to
	 * allow further stuff. For instance, here we could add an asynchronous method
	 * like:
	 * 
	 * public void executeAsync(Object params) { 
	 * 		new Thread(new Runnable() { 
	 * 			public void run() { 
	 * 				execute(params);
	 * 			}
	 * 		}).start();
	 * }
	 * 
	 */

}
