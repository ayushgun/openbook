package gt.trading.openbook.featuregraph;

import gt.trading.openbook.models.DepthData;
import gt.trading.openbook.models.OrderBookData;
import gt.trading.openbook.models.TradeData;

/**
 * Interface for features. Includes methods basic getter and toString methods as
 * well as callback functions.
 */
public interface Feature {
  /**
   * Gets value of the feature.
   *
   * @return the value of the feature (as a double)
   */
  Double getValue();

  /**
   * Converts feature to string.
   *
   * @return the feature as a string
   */
  String toString();

  /**
   * Provides custom logic to update a feature.
   */
  default void update() {
  }

  /**
   * Provides custom logic for features when a listener receives depthData.
   *
   * @param depthData the depthData the listener just received
   * @return false if successful
   */
  default boolean onDepthEvent(DepthData depthData) {
    return false;
  }

  /**
   * Provides custom logic for features when a listener receives tradeData.
   *
   * @param tradeData the tradeData the listener just received
   * @return false if successful
   */
  default boolean onTradeEvent(TradeData tradeData) {
    return false;
  }

  /**
   * Provides custom logic for features when a listener receives orderBookData.
   *
   * @param orderBookData the orderBookData the listener just received
   * @return false if successful
   */
  default boolean onOrderBookEvent(OrderBookData orderBookData) {
    return false;
  }
}
