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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.zip.GZIPInputStream;

import java.net.http.WebSocket;
import java.net.http.WebSocket.Builder;
import java.net.http.WebSocket.Listener;
import java.time.Duration;
import java.net.http.HttpClient;
import java.net.http.*;

import okio.ByteString;

public interface ListenerInterface {
  static final ObjectMapper mapper = new ObjectMapper();
  static WebSocket webSocket = null;
  static final List<String> messageList = new ArrayList<String>();

  /**
   * Prints connection alert to standard output.
   *
   * @param webSocket current websocket connection
   */
  public default void onOpen(final WebSocket webSocket) {
    System.out.println("WebSocket connection established");
    // this.webSocket = webSocket;
    messageList.forEach(message -> {
      webSocket.sendText(message, false);
    });
    messageList.clear();
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
  public static void onMessage(final WebSocket webSocket, final String text) {
    System.out.println("Received message: " + text);
  }

  /**
   * Prints message alert to standard output.
   *
   * @param webSocket current websocket connection
   * @param bytes     message sent from the server
   */
  public default void onMessage(final WebSocket webSocket, final ByteString bytes) {
    String message;
    JsonNode jsonNode;

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
      webSocket.sendText(heartbeat.toString(), false);
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
   * @param t         error that causes failure
   */
  public static void onFailure(final WebSocket webSocket, final Throwable t) {
    System.out.println("WebSocket connection failure: " + t.getMessage());
  }

  /**
   * Prints close alert to standard output.
   *
   * @param webSocket current websocket connection
   * @param code      websocket close status code
   * @param reason    websocket close reason
   */
  public static void onClose(final WebSocket webSocket, final int code,
      final String reason) {
    System.out.println("WebSocket connection closed: " + reason);
  }

  /**
   * Creates a websocket connection to input url.
   * 
   * @param url url to connect to
   * @return websocket client with connection
   */
  default WebSocket createWebSocketConnection(final String url) {
    // Send a handshake connection to the Huobi API
    WebSocket webSocket = HttpClient.newHttpClient().newWebSocketBuilder().connectTimeout(Duration.ZERO)
        .buildAsync(URI.create(url), new Listener() {
          
        }).join();
    return webSocket;
  }

  /**
   * Sends message to websocket if it is open, otherwise adds it to a list to be
   * sent later.
   * 
   * @param message message to send
   */
  default void sendIfOpen(String message) {
    // ! not sure if this is right way to check
    if (webSocket != null) {
      webSocket.sendText(message, false);
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
