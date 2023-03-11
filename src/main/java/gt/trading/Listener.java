package gt.trading;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONArray;

import java.io.IOException;
import java.math.BigDecimal;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * Utility listener class to manage the WebSocket connection with the Huobi API.
 *
 */
public class Listener extends WebSocketListener {

  /**
   * Writes connection alert to standard output.
   *
   * @param webSocket current websocket connection
   * @param response  initial response from server
   */
  public void onOpen(final WebSocket webSocket, final Response response) {
    System.out.println("WebSocket connection established");
    // webSocket.send("{\"sub\": \"market.ethbtc.kline.1min\",\"id\":
    // \"id1\"}");
    // webSocket.send("{\"sub\": \"market.btcusdt.ticker\"}");
    webSocket.send("{\"sub\": \"market.btcusdt.depth.step3\",\"id\": \"id1\"}");
  }

  /**
   * Writes connection alert to standard output.
   *
   * @param webSocket current websocket connection
   * @param text      message sent from the server
   */
  public void onMessage(final WebSocket webSocket, final String text) {
    System.out.println("Received message: " + text);
  }

  /**
   * Writes connection alert to standard output.
   *
   * @param webSocket current websocket connection
   * @param bytes     message sent from the server
   */
  public void onMessage(final WebSocket webSocket, final ByteString bytes) {
    String data;
    try {
      data = new String(Utils.decode(bytes.toByteArray()));
    } catch (IOException e) {
      System.out.println("Receive message error: " + e.getMessage());
      return;
    }
    JSONObject jsonObject = JSON.parseObject(data);
    if (jsonObject.containsKey("ping")) {
      webSocket.send("{\"pong\":" + jsonObject.get("ping") + "}");
    }
    // System.out.println("Received binary message: " + jsonObject);
    if (jsonObject.containsKey("ch")) {
      JSONObject depth = jsonObject.getJSONObject("tick");
      JSONArray bids = (JSONArray) depth.get("bids");

      System.out.println("-------------------");
      System.out.println("Bids:");
      for (int i = 0; i < Math.min(bids.size(), 5); i++) {
        JSONArray order = (JSONArray) bids.get(i);
        BigDecimal price = (BigDecimal) order.get(0);
        BigDecimal quantity = (BigDecimal) order.get(1);
        System.out.println(price + " --- " + quantity);
      }
      JSONArray asks = (JSONArray) depth.get("asks");
      System.out.println("Asks:");
      for (int i = 0; i < Math.min(asks.size(), 5); i++) {
        JSONArray order = (JSONArray) asks.get(i);
        BigDecimal price = (BigDecimal) order.get(0);
        BigDecimal quantity = (BigDecimal) order.get(1);
        System.out.println(price + " --- " + quantity);
      }
      System.out.println("-------------------");
      // System.out.println(gg);
    }

  }

  /**
   * Writes connection alert to standard output.
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
   * Writes connection alert to standard output.
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
