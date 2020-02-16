package jrj.fexco.api;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

import jrj.fexco.api.iot.AddIotCallback;
import jrj.fexco.api.iot.AzureIotSender;
import jrj.fexco.api.iot.IotSender;
import jrj.fexco.api.iot.RemoveIotCallback;
import jrj.fexco.api.iot.UpdateIotCallback;
import jrj.fexco.api.security.TokenAuthenticationFilter;
import jrj.fexco.business.repository.ITodoRepository;
import jrj.fexco.business.repository.hibernate.TodoRepository;

/**
 * Initializer for API application. This class is responsible for initialization
 * of DI mapping and hooks.
 * 
 * @author José Romero
 *
 */
public class ApiInitializer extends ResourceConfig {
	public ApiInitializer() {
		// Register binder for DI
		register(new ApiInitializerBinder());
		register(TokenAuthenticationFilter.class);
		packages(true, "jrj.fexco");

		registerHooks();
	}

	/**
	 * This procedure register "observer" for the "events" (observables) in the API.
	 * It also injects the sender to use.
	 */
	protected void registerHooks() {
		IotSender sender = new AzureIotSender();
		Hooks.subscribe("add", new AddIotCallback(sender));
		Hooks.subscribe("update", new UpdateIotCallback(sender));
		Hooks.subscribe("remove", new RemoveIotCallback(sender));
	}

	/**
	 * Binder class to map DI classes and implementations.
	 * 
	 * @author José Romero
	 *
	 */
	public class ApiInitializerBinder extends AbstractBinder {
		@Override
		protected void configure() {
			bind(TodoRepository.class).to(ITodoRepository.class);
		}
	}
}
