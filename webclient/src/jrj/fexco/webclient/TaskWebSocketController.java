package jrj.fexco.webclient;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import jrj.fexco.webclient.api.iot.IotReceiver;
import jrj.fexco.webclient.websocket.EndPointSpringConfigurator;
import jrj.fexco.webclient.websocket.WebSocketClient;
import jrj.fexco.webclient.websocket.WebSocketClientCollection;

/**
 * WebSocket end point to manage client connections.
 * 
 * <p>
 * Note: This class is marked as a singleton to ensure we only initialize the
 * IoT receiver once. Uses @see EndPointSpringConfigurator to pass the Spring
 * context to the end point.
 * </p>
 * 
 * @author José Romero
 *
 */
@Component
@Scope("singleton")
@ServerEndpoint(value = "/tunnel/task", configurator = EndPointSpringConfigurator.class)
public class TaskWebSocketController {

	/**
	 * Collection of current WebSocket clients. Singleton loaded by DI.
	 */
	@Autowired
	WebSocketClientCollection clients;

	/**
	 * IoT Receiver. Singleton loaded by DI.
	 */
	@Autowired
	IotReceiver receiver;

	/**
	 * Here we initialize the receiver adding an observer and start receiving
	 * messages. As this class is a singleton. We will do this only once.
	 */
	@PostConstruct
	public void init() {
		receiver.addObserver(clients);
		receiver.startReceiving();
	}

	/**
	 * We close the receiver after object destruction.
	 */
	@PreDestroy
	public void onDestroy() {
		if (receiver != null)
			receiver.close();
	}

	/**
	 * Here we add a new client to the collection.
	 * 
	 * @param session WebSocket session.
	 */
	@OnOpen
	public void onOpen(Session session) {
		clients.add(new WebSocketClient(session));
	}

	/**
	 * Here we remove a client from the collection when it has gone.
	 * 
	 * @param reason  Disconnection reason.
	 * @param session WebSocket session to be removed.
	 */
	@OnClose
	public void onClose(CloseReason reason, Session session) {
		clients.remove(new WebSocketClient(session));
	}
}
