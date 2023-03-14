package gt.trading;

public class App {
  public static void main(String[] args) {
    // Initializes the web socket connection
    // This is just test code for now.
    // OrderBook orderBook = new OrderBook();

    MarketListener marketListener = new MarketListener();
    marketListener.createWebSocketConnection("wss://api.huobi.pro/ws");
    Bbo bbo = new Bbo(marketListener);
    TradeDetail tradeDetail = new TradeDetail(marketListener);
  }

}
