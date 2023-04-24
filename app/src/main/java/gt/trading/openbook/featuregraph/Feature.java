package gt.trading.openbook.featuregraph;

import gt.trading.openbook.models.DepthData;
import gt.trading.openbook.models.TradeData;
import gt.trading.openbook.models.OrderBookData;

public interface Feature {
  /**
   * Gets value of the feature.
   * @return a double
   */
  Double getValue();

  /**
   * Converts feature to string.
   * @return a string
   */
  String toString();

  default void update() {}

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
