package gt.trading.huobi.core;

import gt.trading.huobi.listeners.MarketListener;
import gt.trading.huobi.models.DepthData;

/**
 * The DepthEvent class represents a depth event handler for the best bid and
 * ask prices. It subscribes to the depth events from a MarketListener and
 * updates its internal best bid and ask prices accordingly.
 */
public final class DepthEvent {
  private double bestBid = 0.0;
  private double bestAsk = 0.0;

  /**
   * Constructs a DepthEvent instance and subscribes to the depth events from
   * the given MarketListener. The listener's callback processes the received
   * data and updates the best bid and ask prices.
   *
   * @param listener the MarketListener instance to subscribe to depth events
   */
  public DepthEvent(final MarketListener listener) {
    listener.subscribeDepth(data -> {
      updateBestBidAndAsk(data);
    });
  }

  /**
   * Updates the best bid and ask prices based on the received depth data.
   *
   * @param data The received depth data.
   */
  private void updateBestBidAndAsk(final DepthData data) {
    if (data.getAsk() < bestAsk) {
      bestAsk = data.getAsk();
    }

    if (data.getBid() > bestBid) {
      bestBid = data.getBid();
    }
  }
}
