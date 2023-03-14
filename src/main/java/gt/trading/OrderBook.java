package gt.trading;

public class OrderBook {

  public OrderBook() {
    MarketIncrementalListener listener = new MarketIncrementalListener(data -> {
      System.out.println("Asks: " + data.getAsks().size() + ", Bids: "
          + data.getBids().size());
      // incrementUpdateTask(data);
    });
    listener.createWebSocketConnection("wss://api.huobi.pro/feed");
  }

}
