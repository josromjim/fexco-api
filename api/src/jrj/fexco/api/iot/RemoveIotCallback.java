package jrj.fexco.api.iot;

import java.io.IOException;

import jrj.fexco.api.HookCallback;
import jrj.fexco.api.viewmodel.TodoViewModel;

/**
 * Hook callback for deleting todo.
 * 
 * <p>
 * Note: DI on IoTSender
 * </p>
 * 
 * @author José Romero
 *
 */
public class RemoveIotCallback extends HookCallback {
	
	protected IotSender sender;
	
	public RemoveIotCallback(IotSender sender) {
		this.sender = sender;
	}

	@Override
	public void execute(Object params) {
		try {
			sender.send((TodoViewModel) params, "delete");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
