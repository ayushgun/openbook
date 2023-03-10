package gt.trading;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

/**
 * Provides methods to handle events from the Huobi API.
 */
final class Events {
  private static WebSocket webSocket;

  /**
   * Connect to the Huobi websocket API.
   *
   */
  public static void connect() {
    // Send a handshake connection to the Huobi API
    OkHttpClient client = new OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS).build();
    Request request = new Request.Builder().url("wss://api.huobi.pro/ws")
        .build();

    webSocket = client.newWebSocket(request, new Listener());

    // Cleanly end the connection process
    client.dispatcher().executorService().shutdown();
  }
}
