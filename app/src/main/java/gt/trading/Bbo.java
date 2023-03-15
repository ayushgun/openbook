package gt.trading;

import gt.trading.Listener.MarketListener;

public class Bbo {

  private Double bestBid = 0.0;

  private Double bestAsk = 0.0;
  
  /**
   * Best Bid/Offer constructor.
   * 
   * @param listener  takes in a MarketListener object
   */
  public Bbo(MarketListener listener) {
    listener.subscribeBBO(data -> {
      if (data.getAsk() < bestAsk) {
        bestAsk = data.getAsk();
      }
      if (data.getBid() > bestBid) {
        bestBid = data.getBid();
      }
      System.out.println(data);
    });
  }
}
