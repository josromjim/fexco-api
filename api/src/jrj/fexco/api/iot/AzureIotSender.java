package jrj.fexco.api.iot;

import java.io.IOException;
import java.net.URISyntaxException;

import com.google.gson.Gson;
import com.microsoft.azure.sdk.iot.device.DeviceClient;
import com.microsoft.azure.sdk.iot.device.IotHubClientProtocol;
import com.microsoft.azure.sdk.iot.device.IotHubEventCallback;
import com.microsoft.azure.sdk.iot.device.IotHubStatusCode;
import com.microsoft.azure.sdk.iot.device.Message;

import jrj.fexco.api.viewmodel.TodoViewModel;

/**
 * Azure implementation for IoT Sender.
 * 
 * @author Jose
 *
 */
public class AzureIotSender implements IotSender {

	// The device connection string to authenticate the device with your IoT hub.
	private static final String connString = "HostName=jrjfexcoapi.azure-devices.net;DeviceId=TodosDevice;SharedAccessKey=ouPY82zf/8FYXl5gk4Toe1z40Hy00v1Dvof9IirtvGc=";

	// Using the MQTT protocol to connect to IoT Hub
	private static final IotHubClientProtocol protocol = IotHubClientProtocol.MQTT;
	private DeviceClient client;
	private boolean isConnectionOpen = false;

	public AzureIotSender() {
		this.initializeClient();
	}

	private void initializeClient() {
		try {
			// We create the client to the IoT device.
			client = new DeviceClient(connString, protocol);
			this.openConnetion();
		} catch (IllegalArgumentException e) {
			// Log error
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// Log error
			e.printStackTrace();
		} catch (IOException e) {
			// Log error
			e.printStackTrace();
		}
	}

	private void openConnetion() throws IOException {
		try {
			// Open the connection
			client.open();
			isConnectionOpen = true;
		} catch (IOException e) {
			isConnectionOpen = false;
			throw e;
		}
	}

	/**
	 * This class handles the response of the IoT Hub after message submitting.
	 * 
	 * @author José Romero
	 *
	 */
	private class EventCallback implements IotHubEventCallback {
		public void execute(IotHubStatusCode status, Object context) {
			System.out.println("IoT Hub responded to message with status: " + status.name());

			if (context != null) {
				synchronized (context) {
					context.notify();
				}
			}
		}
	}

	@Override
	public void send(TodoViewModel todo, String type) throws IOException {
		Gson gson = new Gson();
		String todoSerialized = gson.toJson(todo);
		Message msg = new Message(todoSerialized);

		// Add a custom application property to the message.
		// An IoT hub can filter on these properties without access to the message body.
		msg.setProperty("operationtype", type);

		// Check if the client is created
		if (client == null) {
			this.initializeClient();
		} else if (!isConnectionOpen) {
			// We ensure the connection is open before sending
			this.openConnetion();
		}

		// Send the message
		if (client != null)
			client.sendEventAsync(msg, new EventCallback(), this);
		else
			throw new IOException("IoT Hub not reachable");
	}

	@Override
	protected void finalize() throws Throwable {
		try {
			// We just close the connection
			if (client != null)
				client.closeNow();
		} catch (IOException e) {
			// Log the error
			e.printStackTrace();
		}
	}
}
