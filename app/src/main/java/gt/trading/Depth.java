package gt.trading;

import gt.trading.Listener.MarketListener;

public class Depth {

  private Double bestBid = 0.0;

  private Double bestAsk = 0.0;
  
  /**
   * Best Bid/Offer constructor.
   * 
   * @param listener  takes in a MarketListener object
   */
  public Depth(MarketListener listener) {
    listener.subscribeDepth(data -> {
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
