package gt.trading;

import java.io.IOException;
import java.util.Map;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * Utility listener class to manage the WebSocket connection with the Huobi API.
 */
public abstract class Listener extends WebSocketListener {
  /**
   * Prints connection alert to standard output.
   *
   * @param webSocket current websocket connection
   * @param response  initial response from server
   */
  public void onOpen(final WebSocket webSocket, final Response response) {
    System.out.println("WebSocket connection established");

    // Subscribe to BTC-USDT depth channel
    JSONObject subscribe = new JSONObject(
        Map.of("sub", "market.btcusdt.depth.step0", "id", "id1"));
    webSocket.send(subscribe.toJSONString());
  }

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
    System.out.println("Message" + message);
    // Reads message 
    // ObjectMapper mapper = new ObjectMapper();
    // JsonNode jsonNode = mapper.readTree(message);
    
    // // Send heartbeat response to ping from server
    // if (jsonNode.has("ping")) {
    //   JsonNode heartbeat = mapper.createObjectNode();
    //   ((ObjectNode) heartbeat).put("pong", jsonNode.get("ping").asText());

    //   //JsonNode heartbeat = new JsonNode(Map.of("pong", jsonNode.get("ping").asText()));
    //   //webSocket.send(heartbeat.toSt);
    // } else {
    //   handleEvent(jsonNode);
    // }
  }

  /**
   * Handles custom logic for each event that is implemented inside the
   * onMessage method.
   * 
   * @param json json object containing data
   */
  public abstract void handleEvent(final JsonNode json);

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
}
