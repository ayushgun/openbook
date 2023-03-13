package gt.trading;

public class App {
  public static void main(String[] args) {
    // Initializes the web socket connection
    // This is just test code for now.
    Connection.connect("wss://api.huobi.pro/feed",
        new MarketIncrementalListener());
  }
}
