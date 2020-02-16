package jrj.fexco.api.iot;

import java.io.IOException;

import jrj.fexco.api.HookCallback;
import jrj.fexco.api.viewmodel.TodoViewModel;

/**
 * Hook callback for adding todo.
 * 
 * <p>
 * Note: DI on IoTSender
 * </p>
 * 
 * @author José Romero
 *
 */
public class AddIotCallback extends HookCallback {
	
	protected IotSender sender;
	
	public AddIotCallback(IotSender sender) {
		this.sender = sender;
	}

	@Override
	public void execute(Object params) {
		try {
			sender.send((TodoViewModel) params, "insert");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
