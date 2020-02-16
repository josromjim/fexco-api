package jrj.fexco.webclient.api.iot;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.microsoft.azure.eventhubs.ConnectionStringBuilder;
import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventhubs.EventHubClient;
import com.microsoft.azure.eventhubs.EventHubException;
import com.microsoft.azure.eventhubs.EventHubRuntimeInformation;
import com.microsoft.azure.eventhubs.EventPosition;
import com.microsoft.azure.eventhubs.PartitionReceiver;

/**
 * This is the implementation of the IoT receiver for Microsoft Azure.
 * 
 * <p>
 * Note: this class is a singleton to ensure we only have one receiver.
 * </p>
 * 
 * @author José Romero
 *
 */
@Component
@Scope("singleton")
public class IotAzureReceiver extends IotReceiver {

	// az iot hub show --query properties.eventHubEndpoints.events.endpoint --name
	// {your IoT Hub name}
	private static final String eventHubsCompatibleEndpoint = "sb://iothub-ns-jrjfexcoap-2919960-5d60e26219.servicebus.windows.net/";

	// az iot hub show --query properties.eventHubEndpoints.events.path --name {your
	// IoT Hub name}
	private static final String eventHubsCompatiblePath = "jrjfexcoapi";

	// az iot hub policy show --name service --query primaryKey --hub-name {your IoT
	// Hub name}
	private static final String iotHubSasKey = "7QdVrmEs+ufSF8eJCdflW4s2otNKfoZL2bQ5RQMOLb4=";
	private static final String iotHubSasKeyName = "service";

	// Track all the PartitionReciever instances created.
	private ArrayList<PartitionReceiver> receivers = new ArrayList<PartitionReceiver>();

	private EventHubClient ehClient = null;
	private ExecutorService executorService = null;
	private Gson gson;

	public IotAzureReceiver() {
		this.gson = new Gson();

		ConnectionStringBuilder connStr;
		try {
			connStr = new ConnectionStringBuilder().setEndpoint(new URI(eventHubsCompatibleEndpoint))
					.setEventHubName(eventHubsCompatiblePath).setSasKeyName(iotHubSasKeyName).setSasKey(iotHubSasKey);

			// Create an EventHubClient instance to connect to the
			// IoT Hub Event Hubs-compatible endpoint.
			executorService = Executors.newSingleThreadExecutor();
			ehClient = EventHubClient.createSync(connStr.toString(), executorService);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EventHubException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Asynchronously create a PartitionReceiver for a partition and then start
	 * reading any messages sent.
	 * 
	 * @param partitionId
	 * @throws EventHubException
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	private void receiveMessages(String partitionId)
			throws EventHubException, ExecutionException, InterruptedException {

		final ExecutorService executorService = Executors.newSingleThreadExecutor();

		// Create the receiver using the default consumer group.
		// We read only messages sent since the time the receiver is created to avoid
		// duplications.
		ehClient.createReceiver(EventHubClient.DEFAULT_CONSUMER_GROUP_NAME, partitionId,
				EventPosition.fromEnqueuedTime(Instant.now())).thenAcceptAsync(receiver -> {
					System.out.println(String.format("Starting receive loop on partition: %s", partitionId));
					System.out.println(String.format("Reading messages sent since: %s", Instant.now().toString()));

					receivers.add(receiver);

					while (true) {
						try {
							// Check for EventData - this methods times out if there is nothing to retrieve.
							Iterable<EventData> receivedEvents = receiver.receiveSync(100);

							// If there is data in the batch, process it.
							if (receivedEvents != null) {
								for (EventData receivedEvent : receivedEvents) {

									String messageStr = new String(receivedEvent.getBytes(), Charset.defaultCharset());

									System.out.println(String.format("Message received:\n %s", messageStr));
									System.out.println(String.format("Application properties (set by device):\n%s",
											receivedEvent.getProperties().toString()));
									System.out.println(String.format("System properties (set by IoT Hub):\n%s\n",
											receivedEvent.getSystemProperties().toString()));

									String operation = (String) receivedEvent.getProperties().get("operationtype");
									IotMessage message = new IotMessage(operation,
											this.gson.fromJson(messageStr, Object.class));
									// Publish to observers
									this.publish(message);
								}
							}
						} catch (EventHubException e) {
							System.out.println("Error reading EventData");
						}
					}
				}, executorService);
	}

	@Override
	public void startReceiving() {

		// Use the EventHubRunTimeInformation to find out how many partitions
		// there are on the hub.
		EventHubRuntimeInformation eventHubInfo;
		try {
			eventHubInfo = ehClient.getRuntimeInformation().get();
			// Create a PartitionReciever for each partition on the hub.
			for (String partitionId : eventHubInfo.getPartitionIds()) {
				receiveMessages(partitionId);
			}
		} catch (InterruptedException e) {
			// Log error
			e.printStackTrace();
		} catch (ExecutionException e) {
			// Log error
			e.printStackTrace();
		} catch (EventHubException e) {
			// Log error
			e.printStackTrace();
		}

	}

	@Override
	public void close() {
		// Shut down cleanly.
		System.out.println("Shutting down IoTReceiver...");
		for (PartitionReceiver receiver : receivers) {
			try {
				receiver.closeSync();
			} catch (EventHubException e) {
				// Log error
				e.printStackTrace();
			}
		}
		try {
			ehClient.closeSync();
		} catch (EventHubException e) {
			// Log error
			e.printStackTrace();
		}
		executorService.shutdown();
	}
}
