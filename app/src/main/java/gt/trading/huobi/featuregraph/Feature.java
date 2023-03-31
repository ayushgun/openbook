package gt.trading.huobi.featuregraph;

import gt.trading.huobi.buckets.DepthData;
import gt.trading.huobi.buckets.TradeData;
import gt.trading.huobi.buckets.OrderBookData;

public interface Feature {

  public Double getValue();

  public String toString();

  default Boolean onDepthEvent(DepthData depthData) {
    return Boolean.FALSE;
  }

  default Boolean onTradeEvent(TradeData tradeData) {
    return Boolean.FALSE;
  }

  default Boolean onOrderBookEvent(OrderBookData orderBookData) {
    return Boolean.FALSE;
  }

}
