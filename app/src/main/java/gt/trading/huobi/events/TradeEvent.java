package gt.trading.huobi.events;

import java.util.ArrayList;
import java.util.logging.Logger;

import gt.trading.huobi.listeners.MarketListener;
import gt.trading.huobi.models.TradeData;

/**
 * The TradeEvent class represents a trade event handler that stores the latest
 * trade data. It subscribes to the trade detail events from a MarketListener
 * and maintains a list of up to 1000 most recent trade data entries.
 */
public class TradeEvent {
  private static final int MAX_TRADE_DATA_SIZE = 1000;
  private final ArrayList<TradeData> tradeDataList;
  private final Logger logger = Logger.getLogger(TradeEvent.class.getName());

  /**
   * Constructs a TradeEvent instance and subscribes to the trade detail events
   * from the given MarketListener. The listener's callback processes the
   * received data and adds it to the trade data list.
   *
   * @param listener the MarketListener instance to subscribe to trade detail
   *                 events
   */
  public TradeEvent(final MarketListener listener) {
    tradeDataList = new ArrayList<>();
    listener.subscribeTradeDetail(data -> {
      if (tradeDataList.size() < MAX_TRADE_DATA_SIZE) {
        tradeDataList.add(data);
      }

      logger.info(data.toString());
    });
  }
}
