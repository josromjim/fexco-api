package jrj.fexco.webclient.api.iot;

import java.util.Observable;

/**
 * This class is the base for IoT receivers. We make it observable, this way we
 * can propagate the received messages to other components.
 * 
 * @author José Romero
 *
 */
public abstract class IotReceiver extends Observable {

	/**
	 * Start receiving messages from the IoT Hub.
	 */
	public abstract void startReceiving();

	/**
	 * Close the receiver.
	 */
	public abstract void close();

	/**
	 * Publish receiver messages to observers.
	 * 
	 * @param param Message received.
	 */
	protected void publish(IotMessage param) {
		this.setChanged();
		this.notifyObservers(param);
	}
}
