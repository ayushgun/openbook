package gt.trading;

import okhttp3.OkHttpClient;

public class OrderBook {
  public OrderBook() {
    OkHttpClient connection = Connection.connect("wss://api.huobi.pro/feed",
        new MarketIncrementalListener());
  }
}
