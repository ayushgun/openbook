package gt.trading;

import java.util.concurrent.TimeUnit;

import gt.trading.Listener.Listener;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Provides methods to handle events from the Huobi API.
 */
public class Connection {
  /**
   * Connect to the Huobi websocket API.
   *
   */
  public static OkHttpClient connect(String url, Listener listener) {
    // Send a handshake connection to the Huobi API
    OkHttpClient client = new OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS).build();
    Request request = new Request.Builder().url(url).build();

    client.newWebSocket(request, listener);

    // Cleanly end the connection process
    client.dispatcher().executorService().shutdown();
    return client;
  }
}
