package gt.trading.huobi.listeners;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * Utility listener class to manage the WebSocket connection with the Huobi API.
 */
public abstract class Listener extends WebSocketListener {
  protected static final ObjectMapper MAPPER = new ObjectMapper();
  private WebSocket webSocket = null;
  private final List<String> messageList = new ArrayList<String>();

  /**
   * Prints connection alert to standard output.
   *
   * @param webSocket current websocket connection
   * @param response  initial response from server
   */
  public void onOpen(final WebSocket webSocket, final Response response) {
    System.out.println("WebSocket connection established");
    this.webSocket = webSocket;
    messageList.forEach(message -> {
      webSocket.send(message);
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
  public void onMessage(final WebSocket webSocket, final String text) {
    System.out.println("Received message: " + text);
  }

  /**
   * Prints message alert to standard output.
   *
   * @param webSocket current websocket connection
   * @param bytes     message sent from the server
   */
  public void onMessage(final WebSocket webSocket, final ByteString bytes) {
    String message;
    JsonNode jsonNode;

    // Decode byte string message to utf-8 string
    try {
      message = new String(decode(bytes));
      // Reads message
      jsonNode = MAPPER.readTree(message);
    } catch (IOException e) {
      System.out.println("Receive message error: " + e.getMessage());
      return;
    }
    // Send heartbeat response to ping from server
    if (jsonNode.has("ping")) {
      // ObjectNode heartbeat = MAPPER.createObjectNode();
      // heartbeat.put("pong", jsonNode.get("ping").asText());
      ObjectMapper MAPPER = new ObjectMapper();
      ObjectNode heartbeat = MAPPER
          .valueToTree(Map.of("pong", jsonNode.get("ping").asText()));
      webSocket.send(heartbeat.toString());
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
  protected abstract void handleEvent(final JsonNode json);

  /**
   * Prints error alert to standard output.
   *
   * @param webSocket current websocket connection
   * @param t         error that causes failure
   * @param response  failure response from server
   */
  public void onFailure(final WebSocket webSocket, final Throwable t,
      final Response response) {
    System.out.println("WebSocket connection failure: " + t.getMessage());
  }

  /**
   * Prints close alert to standard output.
   *
   * @param webSocket current websocket connection
   * @param code      websocket close status code
   * @param reason    websocket close reason
   */
  public void onClosed(final WebSocket webSocket, final int code,
      final String reason) {
    System.out.println("WebSocket connection closed: " + reason);
  }

  /**
   * Creates a websocket connection to input url.
   * 
   * @param url url to connect to
   * @return websocket client with connection
   */
  public OkHttpClient createWebSocketConnection(final String url) {
    // Send a handshake connection to the Huobi API
    OkHttpClient client = new OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS).build();
    Request request = new Request.Builder().url(url).build();

    client.newWebSocket(request, this);

    // Cleanly end the connection process
    client.dispatcher().executorService().shutdown();
    return client;
  }

  protected void sendIfOpen(String message) {
    // ! not sure if this is right way to check
    if (webSocket != null) {
      webSocket.send(message);
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
   * 
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
