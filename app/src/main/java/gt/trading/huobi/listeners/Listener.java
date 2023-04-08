package gt.trading.huobi.listeners;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * The Listener class represents a WebSocket listener that provides methods to
 * establish a WebSocket connection with a server, send messages to the server,
 * and receive messages from the server. It is an abstract class, so it must be
 * subclassed to provide the implementation for onMessage() method. The listener
 * is registered as a WebSocket endpoint using the @ClientEndpoint annotation.
 */
@ClientEndpoint
public abstract class Listener {
  private final Logger logger = Logger.getLogger(Listener.class.getName());
  private final ObjectMapper mapper = new ObjectMapper();
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
  public boolean send(final String message) {
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
   * Sends a JSON message to the connected WebSocket session.
   *
   * @param json the JSON message to send to the server
   * @return true if the message was sent and false if it was added to the
   *         message queue
   */
  public boolean send(final JsonNode json) {
    try {
      String response = mapper.writeValueAsString(json);

      if (session == null || !session.isOpen()) {
        messages.add(response);
        return false;
      }

      session.getBasicRemote().sendText(response);
      return true;
    } catch (JsonProcessingException error) {
      logger.severe(
          "Error processing JSON response to send" + error.getMessage());
    } catch (IOException error) {
      logger.severe("Unable to establish send message to websocket: "
          + error.getMessage());
    }

    return false;
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
   * Handles incoming binary messages by deserializing the received ByteBuffer
   * into a JsonNode object. Logs any errors that occur during deserialization
   * using the Java Util Logging (JUL) library.
   *
   * @param byteBuffer The received ByteBuffer containing the binary message.
   */
  @OnMessage
  public void onMessage(final ByteBuffer byteBuffer) {
    try {
      // Deserialize the binary data into a JSON object
      byte[] byteArray = new byte[byteBuffer.remaining()];
      byteBuffer.get(byteArray);
      JsonNode json = mapper.readTree(byteArray);

      if (json.has("ping")) {
        // Send a heartbeat if the message is a ping
        JsonNode pingCode = json.get("ping");
        ObjectNode heartbeat = mapper.createObjectNode();
        heartbeat.set("pong", pingCode);
        send(heartbeat);
      } else {
        handleEvent(json);
      }
    } catch (IOException error) {
      logger.severe("Error deserializing JSON" + error.getMessage());
    }
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
