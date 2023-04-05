package gt.trading.huobi.featuregraph;

import gt.trading.huobi.buckets.DepthData;
import gt.trading.huobi.buckets.TradeData;
import gt.trading.huobi.buckets.OrderBookData;

public interface Feature {

  public Double getValue();

  public String toString();

  default void update() {
    // do nothing
  }

  default boolean onDepthEvent(DepthData depthData) {
    return false;
  }

  default boolean onTradeEvent(TradeData tradeData) {
    return false;
  }

  default boolean onOrderBookEvent(OrderBookData orderBookData) {
    return false;
  }

}
