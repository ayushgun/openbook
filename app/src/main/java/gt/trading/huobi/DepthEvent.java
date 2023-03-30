package gt.trading.huobi;

import gt.trading.huobi.listeners.MarketListener;

public class DepthEvent {
  private double bestBid = 0.0;
  private double bestAsk = 0.0;

  public DepthEvent(final MarketListener listener) {
    listener.subscribeDepth(data -> {
      if (data.getBestAsk() < bestAsk) {
        bestAsk = data.getBestAsk();
      }
      if (data.getBestBid() > bestBid) {
        bestBid = data.getBestBid();
      }
      System.out.println(data);
    });
  }
}
