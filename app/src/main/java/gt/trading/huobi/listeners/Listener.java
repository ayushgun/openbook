package gt.trading.huobi.listeners;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * The Listener class represents a WebSocket listener that provides methods to
 * establish a WebSocket connection with a server, send messages to the server,
 * and receive messages from the server. It is an abstract class, so it must be
 * subclassed to provide the implementation for onMessage() method. The listener
 * is registered as a WebSocket endpoint using the @ClientEndpoint annotation.
 */
@ClientEndpoint
public abstract class Listener {
  private Logger logger = Logger.getLogger(Listener.class.getName());
  private Session session = null;
  private List<String> messages = new ArrayList<String>();

  /**
   * Creates a websocket connection to the URL.
   *
   * @param uri the url to establish a websocket connection with
   */
  public void connect(final String uri) {
    WebSocketContainer container = ContainerProvider.getWebSocketContainer();

    try {
      session = container.connectToServer(this, URI.create(uri));
      session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE,
          "Connection ended"));
    } catch (DeploymentException | IOException error) {
      logger.severe(
          "Unable to establish a websocket connection: " + error.getMessage());
    }
  }

  /**
   * Sends a text message to the connected WebSocket session.
   *
   * @param message the text message to send to the server
   * @return true if the message was sent and false if it was added to the
   *         message queue
   */
  protected boolean send(final String message) {
    if (session == null || !session.isOpen()) {
      messages.add(message);
      return false;
    }

    try {
      session.getBasicRemote().sendText(message);
      return true;
    } catch (IOException error) {
      logger.severe("Unable to establish send message to websocket: "
          + error.getMessage());
      return false;
    }
  }

  /**
   * Provides custom logic for each event that is implemented inside the
   * onMessage method.
   *
   * @param data the json object containing the event data
   */
  abstract void handleEvent(JsonNode data);

  /**
   * Called when the WebSocket connection is established.
   *
   * @param newSession the active WebSocket session
   */
  @OnOpen
  public void onOpen(final Session newSession) {
    session = newSession;
    logger.info("Connected to WebSocket server");

    for (String message : messages) {
      send(message);
    }

    messages.clear();
  }

  /**
   * Called when a message is received from the WebSocket server.
   *
   * @param message the received message as a string
   */
  @OnMessage
  public void onMessage(final String message) {
    logger.info("Received message: " + message);
  }

  /**
   * Called when the WebSocket connection is closed.
   *
   * @param closeReason the reason for closing the connection
   */
  @OnClose
  public void onClose(final CloseReason closeReason) {
    logger.info("Connection closed: " + closeReason.getReasonPhrase());
  }

  /**
   * Called when an error occurs in the WebSocket connection.
   *
   * @param throwable the exception that caused the error
   */
  @OnError
  public void onError(final Throwable throwable) {
    logger.severe("Error occurred: " + throwable.getMessage());
  }
}
