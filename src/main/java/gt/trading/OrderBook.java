package gt.trading;

import okhttp3.OkHttpClient;

public class OrderBook {
  public OrderBook() {
    
    // Connection for Market Data
    OkHttpClient connection = Connection.connect("wss://api.huobi.pro/feed",
        new MarketIncrementalListener(data -> {

          System.out.println("Market:\n" + data);
        }));
    
    // Connection for Trade Data
    OkHttpClient connection2 = Connection.connect("wss://api.huobi.pro/ws",
        new TradeListener(data -> {

          System.out.println("Trade:\n" + data);
        }));
    
    // Connection for Best Bid/Offer Data
    OkHttpClient connection3 = Connection.connect("wss://api.huobi.pro/ws",
        new BboListener(data -> {

          System.out.println("BBO:\n" + data);
        }));
  }
}
