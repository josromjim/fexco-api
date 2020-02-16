package jrj.fexco.webclient.websocket;

import java.io.IOException;

import javax.websocket.Session;

/**
 * WebSocket client. The class is a kind of facade for Session objects and
 * decoupling the Session objects from our WebSocketClientCollection.
 * 
 * @author José Romero
 *
 */
public class WebSocketClient {
	private final String id;
	private final Session session;

	public WebSocketClient(Session session) {
		this.id = this.toString(); // uses the hash code
		this.session = session;
	}

	/**
	 * Facade to send a message to the client.
	 * 
	 * @param text Message to send.
	 * @throws IOException If the connection is closed.
	 */
	public void sendText(String text) throws IOException {
		this.session.getBasicRemote().sendText(text);
	}

	/**
	 * Id of the object based on hash code.
	 * 
	 * @return Id.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Generates a unique identifier for the object.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((session == null) ? 0 : session.hashCode());
		return result;
	}

	/**
	 * Compares the object based on the inner session to allow us remove the client
	 * from the collection only knowing the session object.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WebSocketClient other = (WebSocketClient) obj;
		if (session == null) {
			if (other.session != null)
				return false;
		} else if (!session.equals(other.session))
			return false;
		return true;
	}
}
