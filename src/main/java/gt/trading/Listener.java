package gt.trading;

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
    System.out.println("Received binary message: " + bytes.hex());
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
