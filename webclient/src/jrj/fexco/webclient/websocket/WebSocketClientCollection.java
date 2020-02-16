package jrj.fexco.webclient.websocket;

import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.function.Consumer;
import java.util.Observer;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.google.gson.Gson;

/**
 * WebSocket clients collection. This class is a repository of @see
 * WebSocketClient.
 * 
 * <p>
 * Notes:
 * <ul>
 * <li>This class is a singleton to ensure we only have one collection in the
 * app.</li>
 * <li>We also use this class as an observer for IoT Receivers. We could
 * refactor this and make a third class to implement the observer and another
 * one to implement a broker to send the messages to the collection, but we
 * simplify all this just making this class as an observer.</li>
 * </ul>
 * </p>
 * 
 * @author José Romero
 *
 */
@Repository
@Scope("singleton")
public class WebSocketClientCollection implements Observer {
	private List<WebSocketClient> clients = new LinkedList<>();

	public void add(WebSocketClient session) {
		synchronized (this.clients) {
			this.clients.add(session);
		}
	}

	public void remove(WebSocketClient session) {
		synchronized (this.clients) {
			this.clients.remove(session);
		}
	}

	public void forEach(Consumer<WebSocketClient> clientConsume) {
		synchronized (this.clients) {
			this.clients.forEach(clientConsume);
		}
	}

	/**
	 * Sends the message to the clients when the observable notifies.
	 */
	@Override
	public void update(Observable receiver, Object param) {
		this.forEach(client -> {
			try {
				Gson gson = new Gson();
				client.sendText(gson.toJson(param));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}
