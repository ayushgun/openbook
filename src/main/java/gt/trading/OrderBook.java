package gt.trading;

import okhttp3.OkHttpClient;

public class OrderBook {
  public OrderBook() {
    OkHttpClient connection = Connection.connect("wss://api.huobi.pro/feed",
        new MarketIncrementalListener(data -> {

          System.out.println("Market:\n" + data);
        }));
    OkHttpClient connection2 = Connection.connect("wss://api.huobi.pro/ws",
        new TradeListener(data -> {

          System.out.println("Trade:\n" + data);
        }));
    OkHttpClient connection3 = Connection.connect("wss://api.huobi.pro/ws",
        new BboListener(data -> {

          System.out.println("BBO:\n" + data);
        }));
  }
}
