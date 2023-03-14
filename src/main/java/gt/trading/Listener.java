package gt.trading;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;

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
  protected static final ObjectMapper objectMapper = new ObjectMapper();
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

    // Decode byte string message to utf-8 string
    try {
      message = new String(Utils.decode(bytes));
    } catch (IOException e) {
      System.out.println("Receive message error: " + e.getMessage());
      return;
    }

    JSONObject json = JSON.parseObject(message);

    // Send heartbeat response to ping from server
    if (json.containsKey("ping")) {
      JSONObject heartbeat = new JSONObject(Map.of("pong", json.get("ping")));
      webSocket.send(heartbeat.toJSONString());
    } else {
      handleEvent(message);
    }
  }

  /**
   * Handles custom logic for each event that is implemented inside the
   * onMessage method.
   * 
   * @param json json object containing data
   */
  protected abstract void handleEvent(final String json);

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
}
