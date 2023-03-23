package gt.trading.huobi.listeners;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.rmi.Remote;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.websocket.ClientEndpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import okio.ByteString;

@ClientEndpoint
public interface JavaxListener {
  static final ObjectMapper mapper = new ObjectMapper();
  static Session session = null;
  static final List<String> messageList = new ArrayList<String>();

  /**
   * Prints connection alert to standard output.
   *
   * @param webSocket current websocket connection
   */
  @OnOpen
  public default void onOpen(final Session session,
      final EndpointConfig endpointConfig) {
    System.out.println("WebSocket connection established");
    final RemoteEndpoint.Basic remote = session.getBasicRemote();
    session.addMessageHandler(new MessageHandler.Whole<String>() {
      public void onMessage(String message) {
        messageList.forEach(m -> {
          try {
            remote.sendText(m);
          } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        });
        messageList.clear();
      }
    });
    // messageList.forEach(message -> {
    // remote.sendString(message);
    // });
    // messageList.clear();
  }

  /**
   * Handles custom logic for each event that is implemented inside the
   * onMessage method.
   * 
   * @param json json object containing data
   */
  // protected abstract void subscribe();

  /**
   * Prints message alert to standard output.
   *
   * @param webSocket current websocket connection
   * @param text      message sent from the server
   */
  @OnMessage
  public static void onMessage(final Session session, final String text) {
    System.out.println("Received message: " + text);
  }

  /**
   * Prints message alert to standard output.
   *
   * @param webSocket current websocket connection
   * @param bytes     message sent from the server
   */
  @OnMessage
  public default void onMessage(final Session session, final ByteString bytes) {
    String message;
    JsonNode jsonNode;
    final RemoteEndpoint.Basic remote = session.getBasicRemote();

    // Decode byte string message to utf-8 string
    try {
      message = new String(decode(bytes));
      // Reads message
      jsonNode = mapper.readTree(message);
    } catch (IOException e) {
      System.out.println("Receive message error: " + e.getMessage());
      return;
    }
    // Send heartbeat response to ping from server
    if (jsonNode.has("ping")) {
      ObjectMapper mapper = new ObjectMapper();
      ObjectNode heartbeat = mapper
          .valueToTree(Map.of("pong", jsonNode.get("ping").asText()));
      try {
        remote.sendText(heartbeat.toString(), true);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    } else {
      handleEvent(jsonNode);
    }
  }

  /**
   * Handles custom logic for each event that is implemented inside the
   * onMessage method.
   * 
   * @param json json object containing data
   */
  abstract void handleEvent(final JsonNode json);

  /**
   * Prints error alert to standard output.
   *
   * @param t error that causes failure
   */
  @OnError
  public static void onFailure(final Session session, final Throwable t) {
    System.out.println("WebSocket connection failure: " + t.getMessage());
  }

  /**
   * Prints close alert to standard output.
   *
   * @param session current websocket connection
   * @param code    websocket close status code
   * @param reason  websocket close reason
   */
  @OnClose
  public static void onClose(final Session session, final int code,
      final String reason) {
    System.out.println("WebSocket connection closed: " + reason);
  }

  /**
   * Creates a websocket connection to input url.
   * 
   * @param url url to connect to
   * @return websocket client with connection
   */
  default WebSocketContainer createWebSocketConnection(final String url) {
    // Send a handshake connection to the Huobi API
    WebSocketContainer container = javax.websocket.ContainerProvider
        .getWebSocketContainer();
    try {
      container.connectToServer(this, URI.create(url));
    } catch (Exception e) {
      System.out.println("WebSocket connection failure: " + e.getMessage());
    }
    return container;
  }

  /**
   * Sends message to websocket if it is open, otherwise adds it to a list to be
   * sent later.
   * 
   * @param message message to send
   */
  default void sendIfOpen(final String message) {
    final RemoteEndpoint.Basic remote = session.getBasicRemote();
    if (remote != null) {
      try {
        remote.sendText(message);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    } else {
      messageList.add(message);
    }
  }

  /**
   * Decodes byte string to byte array through decompression.
   * 
   * @param data byte string from websocket
   * @return byte array representation of byte string
   * @throws IOException if there is an error decompressing the streams
   */
  private static byte[] decode(final ByteString data) throws IOException {
    ByteArrayInputStream bais = new ByteArrayInputStream(data.toByteArray());
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
    int count;
    byte[] data = new byte[1024];

    while ((count = gis.read(data, 0, 1024)) != -1) {
      os.write(data, 0, count);
    }

    gis.close();
  }

}