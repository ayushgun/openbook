package gt.trading.huobi.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
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
    listener.connect("wss://api-aws.huobi.pro/ws");
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

  private void processIncrementalUpdate(final OrderBookData data) {
    if ("REFRESH".equals(data.getAction())) {
      long snapshotSeqNum = data.getSeqNum();
      List<OrderBookData> preUpdate = new ArrayList<>(updateQueue.size());
      updateQueue.drainTo(preUpdate);

      boolean finished = false;
      int index = 0;

      for (OrderBookData preData : preUpdate) {
        index++;
        long preSeqNum = preData.getPrevSeqNum();
        long seqNum = preData.getSeqNum();
        logger.info("-> Data with previous sequence number " + preSeqNum
            + ", sequence number " + seqNum + ", snapshot " + snapshotSeqNum);

        if (preSeqNum < snapshotSeqNum) {
          continue;
        }

        if (preSeqNum > snapshotSeqNum) {
          break;
        }

        data.getBids().forEach(priceLevel -> bids.put(priceLevel.getPrice(),
            priceLevel.getAmount()));
        data.getAsks().forEach(priceLevel -> asks.put(priceLevel.getPrice(),
            priceLevel.getAmount()));

        finished = true;
        lastSeqNum = snapshotSeqNum;
        incrementUpdate(preData);
        logger.info("Finished comparing");
        break;
      }

      if (!finished) {
        listener.refresh();
        return;
      }

      for (int i = index; i < preUpdate.size(); i++) {
        OrderBookData preData = preUpdate.get(i);
        incrementUpdate(preData);
      }
    } else {
      if (lastSeqNum < 0) {
        updateQueue.add(data);
        return;
      }

      incrementUpdate(data);
    }
  }

  private void incrementUpdate(final OrderBookData data) {
    double prevSeqNum = data.getPrevSeqNum();

    if (prevSeqNum > lastSeqNum) {
      listener.refresh();
      logger.warning("<- Missed message with previous sequence number "
          + prevSeqNum + ", snapshot " + lastSeqNum);

      lastSeqNum = -1L;
      return;
    }

    if (prevSeqNum < lastSeqNum) {
      return;
    }

    lastSeqNum = data.getSeqNum();

    if (data.getAsks() != null && data.getAsks().size() > 0) {
      for (PriceLevel priceLevel : data.getAsks()) {
        if (priceLevel.getAmount() <= 0) {
          asks.remove(priceLevel.getPrice());
        } else {
          asks.put(priceLevel.getPrice(), priceLevel.getAmount());
        }
      }
    }

    if (data.getBids() != null && data.getBids().size() > 0) {
      for (PriceLevel priceLevel : data.getBids()) {
        if (priceLevel.getAmount() <= 0) {
          bids.remove(priceLevel.getPrice());
        } else {
          bids.put(priceLevel.getPrice(), priceLevel.getAmount());
        }
      }
    }
  }

  private OrderBookData getDepth() {
    Iterator<Entry<Double, Double>> askIterator = asks.entrySet().iterator();
    List<PriceLevel> askLevelList = new ArrayList<>();
    while (askIterator.hasNext()) {
      Entry<Double, Double> entry = askIterator.next();
      double price = entry.getKey();
      double amount = entry.getValue();
      askLevelList
          .add(PriceLevel.builder().amount(amount).price(price).build());
    }

    Iterator<Entry<Double, Double>> bidIterator = bids.entrySet().iterator();
    List<PriceLevel> bidLevelList = new ArrayList<>();
    while (bidIterator.hasNext()) {
      Entry<Double, Double> entry = bidIterator.next();
      double price = entry.getKey();
      double amount = entry.getValue();
      bidLevelList
          .add(PriceLevel.builder().amount(amount).price(price).build());
    }

    return OrderBookData.builder().asks(askLevelList).bids(bidLevelList)
        .build();
  }

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

    askLevels.forEach(ask -> System.out
        .println("ASK\t" + ask.getPrice() + "\t" + ask.getAmount()));
    bidLevels.forEach(bid -> System.out
        .println("BID\t" + bid.getPrice() + "\t" + bid.getAmount()));
  }
}
