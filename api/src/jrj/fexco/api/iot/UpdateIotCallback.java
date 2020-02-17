package jrj.fexco.api.iot;

import java.io.IOException;

import jrj.fexco.api.HookCallback;
import jrj.fexco.api.viewmodel.TodoViewModel;

/**
 * Hook callback for updating todo.
 * 
 * <p>
 * Note: DI on IoTSender
 * </p>
 * 
 * @author José Romero
 *
 */
public class UpdateIotCallback extends HookCallback {
	
	protected IotSender sender;
	
	public UpdateIotCallback(IotSender sender) {
		this.sender = sender;
	}

	@Override
	public void execute(Object params) {
		try {
			sender.send((TodoViewModel) params, "update");
		} catch (IOException e) {
			// Log the error and let the API run
			e.printStackTrace();
		}
	}

}
