package gt.trading.huobi.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import gt.trading.huobi.listeners.OrderBookListener;
import gt.trading.huobi.models.OrderBookData;
import gt.trading.huobi.models.PriceLevel;

/**
 * The OrderBook class represents an order book for the Huobi exchange,
 * providing functionality to manage and process real-time market data. It
 * maintains an accurate representation of the order book for the BTC/USDT
 * instrument by handling events, data models, and listeners to process market
 * data from various Huobi APIs, such as WebSocket events.
 *
 * The order book keeps track of bids and asks using TreeMap structures. An
 * OrderBookListener is used to connect to the WebSocket API, subscribe to
 * market data updates, and handle incoming messages.
 */
public class OrderBook {
  private volatile LinkedBlockingQueue<OrderBookData> updateQueue;
  private volatile Map<Double, Double> bids = new TreeMap<>(
      Comparator.reverseOrder());
  private volatile Map<Double, Double> asks = new TreeMap<>();
  private long lastSeqNum = -1L;
  private boolean firstStart = true;
  private OrderBookListener listener;
  private final Logger logger = Logger
      .getLogger(OrderBookListener.class.getName());

  /**
   * Constructs a new OrderBook instance, sets up an OrderBookListener, and
   * subscribes to incremental order book data updates.
   */
  public OrderBook() {
    updateQueue = new LinkedBlockingQueue<>();
    listener = new OrderBookListener();
    listener.connect("wss://api-aws.huobi.pro/feed");
    final int maxDisplayDepth = 10;

    listener.subscribeMbp(data -> {
      if (firstStart) {
        listener.refresh();
        firstStart = false;
      }

      processIncrementalUpdate(data);
      display(maxDisplayDepth);
    });
  }

  /**
   * Stops the execution of the OrderBook instance by closing the
   * OrderBookListener's WebSocket connection. This method ensures that the
   * listener no longer processes incoming market data updates from the Huobi
   * WebSocket API.
   */
  public void stop() {
    listener.close();
  }

  /**
   * Processes the incremental updates of the order book data. Handles the cases
   * when a refresh is needed, and ensures that all updates are processed in the
   * correct order.
   *
   * @param data An OrderBookData object containing updated bids and asks.
   */
  private void processIncrementalUpdate(final OrderBookData data) {
    OrderBookData.Action action = data.getAction();
    long snapshotSeqNum = data.getSeqNum();

    if (OrderBookData.Action.REFRESH == action) {
      List<OrderBookData> preUpdate = new ArrayList<>(updateQueue.size());
      updateQueue.drainTo(preUpdate);

      boolean finished = false;
      int index = 0;

      for (OrderBookData preData : preUpdate) {
        index++;
        long preSeqNum = preData.getPrevSeqNum();
        logger.info("Data with previous sequence number " + preSeqNum
            + ", sequence number " + preData.getSeqNum() + ", snapshot "
            + snapshotSeqNum);

        if (preSeqNum >= snapshotSeqNum) {
          updatePriceLevels(data.getBids(), bids);
          updatePriceLevels(data.getAsks(), asks);

          finished = true;
          lastSeqNum = snapshotSeqNum;
          incrementUpdate(preData);
          logger.info("Finished comparing");

          break;
        }
      }

      if (!finished) {
        listener.refresh();
        return;
      }

      // Process remaining updates in the queue
      for (int i = index; i < preUpdate.size(); i++) {
        incrementUpdate(preUpdate.get(i));
      }
    } else {
      if (lastSeqNum < 0) {
        updateQueue.add(data);
      } else {
        incrementUpdate(data);
      }
    }
  }

  /**
   * Processes an individual incremental update for the order book data. Updates
   * the bids and asks maps based on the data provided.
   *
   * @param data An OrderBookData object containing updated bids and asks.
   */
  private void incrementUpdate(final OrderBookData data) {
    double prevSeqNum = data.getPrevSeqNum();

    if (prevSeqNum > lastSeqNum) {
      listener.refresh();
      logger.warning("Missed message with previous sequence number "
          + prevSeqNum + ", snapshot " + lastSeqNum);

      lastSeqNum = -1L;
      return;
    }

    if (prevSeqNum < lastSeqNum) {
      return;
    }

    lastSeqNum = data.getSeqNum();

    updatePriceLevels(data.getAsks(), asks);
    updatePriceLevels(data.getBids(), bids);
  }

  /**
   * Updates the specified price levels map with the new price levels data.
   *
   * @param priceLevels A list of PriceLevel objects to update the map with.
   * @param targetMap   The map to be updated with new price levels.
   */
  private void updatePriceLevels(final List<PriceLevel> priceLevels,
      final Map<Double, Double> targetMap) {
    if (priceLevels != null && !priceLevels.isEmpty()) {
      for (PriceLevel priceLevel : priceLevels) {
        double price = priceLevel.getPrice();
        double amount = priceLevel.getAmount();

        if (amount <= 0) {
          targetMap.remove(price);
        } else {
          targetMap.put(price, amount);
        }
      }
    }
  }

  /**
   * Retrieves the current depth of the order book by converting the bids and
   * asks maps into lists of PriceLevel objects.
   *
   * @return An OrderBookData instance containing the current depth of the order
   *         book.
   */
  private OrderBookData getDepth() {
    List<PriceLevel> askLevelList = mapToPriceLevelList(asks);
    List<PriceLevel> bidLevelList = mapToPriceLevelList(bids);

    return OrderBookData.builder().asks(askLevelList).bids(bidLevelList)
        .build();
  }

  /**
   * Converts a map of price and amount into a list of PriceLevel objects.
   *
   * @param priceMap A map containing price and amount values.
   * @return A list of PriceLevel objects created from the input map.
   */
  private List<PriceLevel> mapToPriceLevelList(
      final Map<Double, Double> priceMap) {
    List<PriceLevel> priceLevelList = new ArrayList<>();

    for (Entry<Double, Double> entry : priceMap.entrySet()) {
      double price = entry.getKey();
      double amount = entry.getValue();
      priceLevelList
          .add(PriceLevel.builder().amount(amount).price(price).build());
    }

    return priceLevelList;
  }

  /**
   * Displays the top levels of the order book, limited by the provided
   * maxDisplayDepth. The method prints the top bid and ask levels in a tabular
   * format.
   *
   * @param maxDisplayDepth The maximum number of bid and ask levels to display.
   */
  private void display(final int maxDisplayDepth) {
    OrderBookData depthData = getDepth();

    List<PriceLevel> dataAsks = depthData.getAsks();
    List<PriceLevel> dataBids = depthData.getBids();

    if (dataAsks.size() < maxDisplayDepth
        || dataBids.size() < maxDisplayDepth) {
      return;
    }

    List<PriceLevel> askLevels = dataAsks.subList(0, maxDisplayDepth);
    List<PriceLevel> bidLevels = dataBids.subList(0, maxDisplayDepth);
    Collections.reverse(askLevels);
    System.out.printf("%-10.10s  %-10.10s  %-10.10s%n", "SIDE", "PRICE",
        "AMOUNT");

    askLevels.forEach(ask -> System.out.printf("%-10.10s  %-10.10s  %-10.10s%n",
        "ASK", ask.getPrice(), ask.getAmount()));
    bidLevels.forEach(bid -> System.out.printf("%-10.10s  %-10.10s  %-10.10s%n",
        "BID", bid.getPrice(), bid.getAmount()));
  }
}
