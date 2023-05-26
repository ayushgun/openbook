package gt.trading.openbook.featuregraph;

import gt.trading.openbook.models.DepthData;
import gt.trading.openbook.models.TradeData;
import gt.trading.openbook.models.OrderBookData;

/**
 * Interface for features.
 * Includes methods basic getter and toString methods as well as
 * callback functions.
 */
public interface Feature {
  /**
   * Gets value of the feature.
   *
   * @return a double
   */
  Double getValue();

  /**
   * Converts feature to string.
   *
   * @return a string
   */
  String toString();

  /**
   * Provides custom logic to update a feature.
   */
  default void update() { }

  /**
   * Provides custom logic for features when a listener receives depthData.
   *
   * @param depthData
   * @return false
   */
  default boolean onDepthEvent(DepthData depthData) {
    return false;
  }

  /**
   * Provides custom logic for features when a listener receives tradeData.
   *
   * @param tradeData
   * @return false
   */
  default boolean onTradeEvent(TradeData tradeData) {
    return false;
  }

  /**
   * Provides custom logic for features when a listener receives orderBookData.
   *
   * @param orderBookData
   * @return false
   */
  default boolean onOrderBookEvent(OrderBookData orderBookData) {
    return false;
  }
}
