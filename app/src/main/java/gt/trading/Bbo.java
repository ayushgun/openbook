package gt.trading;

public class Bbo {
  public Bbo(MarketListener listener) {
    listener.subscribeBBO(data -> {
      System.out.println(data);
    });
  }
}
