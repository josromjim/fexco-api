package jrj.fexco.api.iot;

import java.io.IOException;

import jrj.fexco.api.viewmodel.TodoViewModel;

/**
 * Interface for IoT senders.
 * 
 * @author José Romero
 *
 */
public interface IotSender {
	
	
	public void send(TodoViewModel todo, String type) throws IOException;
}
