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
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import javax.websocket.server.ServerEndpoint;

import okio.ByteString;

@ServerEndpoint("/websocket")
public abstract class WebSocket {
  private Session session = null;
  protected static final ObjectMapper mapper = new ObjectMapper();
  private final List<String> messageList = new ArrayList<String>();

  /**
   * Prints connection alert to standard output.
   *
   * @param session current websocket connection
   */
  @OnOpen
  public void onOpen(final Session session) {
    System.out.println("WebSocket connection established");
    this.session = session;
    messageList.forEach(message -> {
      session.getAsyncRemote().sendText(message);
    });
    messageList.clear();
  }

  /**
   * Prints message alert to standard output.
   *
   * @param session current websocket connection
   * @param text      message sent from the server
   */
  public void onMessage(final Session session, final String text) {
    System.out.println("Received message: " + text);
  }

  /**
   * Prints message alert to standard output.
   *
   * @param session current websocket connection
   * @param bytes     message sent from the server
   */
  public void onMessage(final Session session, final ByteString bytes) {
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
      session.getAsyncRemote().sendText(heartbeat.toString());
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
   */
  public void onFailure(final WebSocket webSocket, final Throwable t) {
    System.out.println("WebSocket connection failure: " + t.getMessage());
  }

  /**
   * Prints connection alert to standard output.
   *
   * @param session current websocket connection
   */
  @OnClose
  public void onClose(final Session session) {
    System.out.println("WebSocket connection closed");
  }
  
  /**
   * Creates a websocket connection to input url.
   * 
   * @param url url to connect to
   * @return websocket client with connection
   */
  public WebSocketContainer createWebSocketConnection(final String url) {
    // Send a handshake connection to the Huobi API
    WebSocketContainer container = ContainerProvider.getWebSocketContainer();
    try {
      container.connectToServer(this, URI.create(url));
    } catch (DeploymentException | IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    // OkHttpClient client = new OkHttpClient.Builder()
    //     .readTimeout(0, TimeUnit.MILLISECONDS).build();
    // Request request = new Request.Builder().url(url).build();

    container.setAsyncSendTimeout(0);

    // client.newWebSocket(request, this);

    // // Cleanly end the connection process
    // client.dispatcher().executorService().shutdown();
    // return client;
    return container;
  }

  /**
   * Sends message to websocket if it is open, otherwise adds it to a list to be
   * sent later.
   * 
   * @param message message to send
   */
  protected void sendIfOpen(String message) {
    // ! not sure if this is right way to check
    if (session != null) {
      session.getAsyncRemote().sendText(message);
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
