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

	public AzureIotSender() {

		try {
			// We create the client to the IoT device.
			client = new DeviceClient(connString, protocol);
		} catch (IllegalArgumentException e) {
			// Log error
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// Log error
			e.printStackTrace();
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

			try {
				// We just close the connection
				client.closeNow();
			} catch (IOException e) {
				// Log the error
				e.printStackTrace();
			}
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

		// Open the connection
		client.open();

		// Send the message
		client.sendEventAsync(msg, new EventCallback(), this);
	}
}
