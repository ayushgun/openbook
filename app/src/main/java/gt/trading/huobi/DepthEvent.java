package gt.trading.huobi;

import gt.trading.huobi.listeners.MarketListener;

public class DepthEvent {
  private Double bestBid = 0.0;
  private Double bestAsk = 0.0;

  public DepthEvent(final MarketListener listener) {
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
