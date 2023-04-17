package gt.trading.huobi.listeners;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * The Listener class represents a WebSocket listener that provides methods to
 * establish a WebSocket connection with a server, send messages to the server,
 * and receive messages from the server. It is an abstract class, so it must be
 * subclassed to provide the implementation for handleEvent() method. The
 * listener is registered as a WebSocket endpoint.
 */
@ClientEndpoint
public abstract class Listener {
  private final Logger logger = Logger.getLogger(Listener.class.getName());
  private final ObjectMapper mapper = new ObjectMapper();
  private Session session = null;
  private List<String> messages = new ArrayList<String>();

  /**
   * Closes the WebSocket connection if it's open. The connection is closed with
   * a normal closure code and a message indicating the reason for closing.
   */
  public void close() {
    if (session != null && session.isOpen()) {
      try {
        CloseReason closeReason = new CloseReason(
            CloseReason.CloseCodes.NORMAL_CLOSURE, "Closing connection");
        session.close(closeReason);
      } catch (IOException e) {
        logger.severe("Unable to close connection");
      }
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
  protected abstract void handleEvent(JsonNode data);

  /**
   * Called when the WebSocket connection is established.
   *
   * @param newSession the active WebSocket session
   */
  @OnOpen
  public void onOpen(final Session newSession) {
    session = newSession;
    logger
        .info("Connected to WebSocket server at " + newSession.getRequestURI());

    messages.forEach(message -> send(message));
    messages.clear();
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
      String incomingMessage = new String(decode(byteBuffer));
      JsonNode json = mapper.readTree(incomingMessage);

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

  /**
   * Decodes byte buffer to byte array through decompression.
   *
   * @param data byte buffer from websocket
   * @return byte array representation of byte buffer
   * @throws IOException if there is an error decompressing the streams
   */
  private static byte[] decode(final ByteBuffer data) throws IOException {
    ByteArrayInputStream bais = new ByteArrayInputStream(data.array());
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    decompress(bais, baos);
    baos.flush();
    baos.close();
    bais.close();

    return baos.toByteArray();
  }

  /**
   * Takes an input stream is and an output stream os and decompresses the gzip
   * compressed.
   *
   * @param is input stream containing compressed data
   * @param os output stream to write decompressed data to
   * @throws IOException if there is an error reading or writing to the streams
   */
  private static void decompress(final InputStream is, final OutputStream os)
      throws IOException {
    GZIPInputStream gis = new GZIPInputStream(is);
    final int kilobyteSize = 1024;
    int count;

    byte[] data = new byte[kilobyteSize];

    while ((count = gis.read(data, 0, kilobyteSize)) != -1) {
      os.write(data, 0, count);
    }

    gis.close();
  }

}
