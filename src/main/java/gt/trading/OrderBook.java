package gt.trading;

import okhttp3.OkHttpClient;

public class OrderBook {
  private int count = 0;

  public OrderBook() {
    // OkHttpClient connection = Connection.connect("wss://api.huobi.pro/feed",
    // new MarketIncrementalListener(data -> {

    // System.out.println("GGG:\n" + data);
    // }));

    MarketIncrementalListener listener = new MarketIncrementalListener(data -> {
      // System.out.println("GGG:\n" + data);
      // this.count += 1;
      // System.out.println(this.count);
      // if (this.count == 20) {
      // listener.requestRefresh();
      // }
    });
    listener.createWebSocketConnection("wss://api.huobi.pro/feed");
    for (int i = 0; i < 10000000; i++) {

    }
    listener.requestRefresh();
  }
}
