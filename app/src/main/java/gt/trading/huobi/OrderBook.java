package gt.trading.huobi;

import gt.trading.huobi.buckets.OrderBookData;
import gt.trading.huobi.buckets.PriceLevel;
import gt.trading.huobi.listeners.FeedListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.LinkedBlockingQueue;


public class OrderBook {
  private volatile LinkedBlockingQueue<OrderBookData> updateQueue = new LinkedBlockingQueue<>();

  private volatile Map<Double, Double> bidsMap = new TreeMap<>(
      Comparator.reverseOrder());

  private volatile Map<Double, Double> asksMap = new TreeMap<>();

  private long lastSeqNum = -1L;

  private boolean isFirst = true;

  private FeedListener listener;

  /**
   * OrderBook constructor.
   */
  public OrderBook() {
    listener = new FeedListener();
    listener.createWebSocketConnection("wss://api.huobi.pro/feed");
    listener.subscribeMbpIncremental(data -> {

      if (isFirst) {
        listener.requestRefresh();
        isFirst = false;
      }

      incrementUpdateTask(data);
      showCasePrint();

    });
  }

  /**
   * Increment update task.
   * 
   * @param data OrderBookData object
   */
  private void incrementUpdateTask(final OrderBookData data) {
    if ("REFRESH".equals(data.getAction())) {
      // Save the sequence number of this refresh event.
      Long snapshotSeqNum = data.getSeqNum();

      List<OrderBookData> preUpdateList = new ArrayList<>(updateQueue.size());
      // Extract the updates that were saved in UPDATE_QUEUE.
      updateQueue.drainTo(preUpdateList);

      boolean isFinish = false;
      int index = 0;
      for (OrderBookData preData : preUpdateList) {
        index++;

        Long preSeqNum = preData.getPrevSeqNum();

        System.out.println("data:::: preSeqNum:" + preSeqNum + "  seqNum:"
            + preData.getSeqNum() + "  snapshot:" + snapshotSeqNum);

        // Have updated all increments up to the refresh event, thus can break.
        if (preSeqNum.compareTo(snapshotSeqNum) == 0) {
          data.getBids().forEach(priceLevel -> {
            bidsMap.put(priceLevel.getPrice(), priceLevel.getAmount());
          });

          data.getAsks().forEach(priceLevel -> {
            asksMap.put(priceLevel.getPrice(), priceLevel.getAmount());
          });

          isFinish = true;
          lastSeqNum = snapshotSeqNum;

          incrementUpdate(preData);
          System.out.println("....compare finish....");
          break;
        }

        // Ignore the updates that were before the refresh.
        if (preSeqNum.compareTo(snapshotSeqNum) < 0) {
          System.out
              .println(" ignore message: preSeqNum:" + preSeqNum + "  seqNum:"
                  + preData.getSeqNum() + "  snapshot:" + snapshotSeqNum);
          continue;
        }

        // preSeqNum > snapshotSeqNum, meaning some messages were lost.
        if (preSeqNum.compareTo(snapshotSeqNum) > 0) {
          System.out.println(
              "find incr message preSeqNum > snapshot seqNum....    message:"
                  + preSeqNum + "   snapshot:" + snapshotSeqNum);
          break;
        }

      }

      // The refresh update was unsuccessful, try again.
      if (!isFinish) {
        listener.requestRefresh();
        return;
      }

      for (int i = index; i < preUpdateList.size(); i++) {
        OrderBookData preData = preUpdateList.get(i);
        incrementUpdate(preData);
      }

    } else {
      // if lastSeqNum < 0, it means we haven't refreshed yet, then we put it in
      // the queue first.
      if (lastSeqNum < 0) {
        updateQueue.add(data);
        return;
      }

      // Actual Incremental update.
      incrementUpdate(data);
    }

  }

  /**
   * Increment update.
   * 
   * @param data OrderBookData object
   */
  private void incrementUpdate(final OrderBookData data) {
    // the newest prevSeqNum greater than the saved lastSeqNum, meaning that
    // some message was lost.
    if (data.getPrevSeqNum() > lastSeqNum) {
      listener.requestRefresh();
      System.out.println(" miss message ::: message:" + data.getPrevSeqNum()
          + "   snapshot:" + lastSeqNum);
      lastSeqNum = -1L;
      return;
    }

    if (data.getPrevSeqNum() < lastSeqNum) {
      return;
    }

    lastSeqNum = data.getSeqNum();

    if (data.getAsks() != null && data.getAsks().size() > 0) {
      for (PriceLevel level : data.getAsks()) {
        if (level.getAmount() <= 0) {
          asksMap.remove(level.getPrice());
        } else {
          asksMap.put(level.getPrice(), level.getAmount());
        }

        // if (level.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
        //   asksMap.remove(level.getPrice());
        // } else {
        //   asksMap.put(level.getPrice(), level.getAmount());
        // }
      }
    }

    if (data.getBids() != null && data.getBids().size() > 0) {
      for (PriceLevel level : data.getBids()) {
        if (level.getAmount() <= 0) {
          bidsMap.remove(level.getPrice());
        } else {
          bidsMap.put(level.getPrice(), level.getAmount());
        }

        // if (level.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
        //   bidsMap.remove(level.getPrice());
        // } else {
        //   bidsMap.put(level.getPrice(), level.getAmount());
        // }
      }
    }

  }

  /**
   * Gets the depth.
   * 
   * @return  OrderBookData object
   */
  public OrderBookData getDepth() {
    Iterator<Entry<Double, Double>> askIterator = asksMap.entrySet()
        .iterator();
    List<PriceLevel> askLevelList = new ArrayList<>();
    while (askIterator.hasNext()) {
      Entry<Double, Double> entry = askIterator.next();
      Double price = entry.getKey();
      Double amount = entry.getValue();
      askLevelList
          .add(PriceLevel.builder().amount(amount).price(price).build());
    }

    Iterator<Entry<Double, Double>> bidIterator = bidsMap.entrySet()
        .iterator();
    List<PriceLevel> bidLevelList = new ArrayList<>();
    while (bidIterator.hasNext()) {
      Entry<Double, Double> entry = bidIterator.next();
      Double price = entry.getKey();
      Double amount = entry.getValue();
      bidLevelList
          .add(PriceLevel.builder().amount(amount).price(price).build());
    }

    return OrderBookData.builder().asks(askLevelList).bids(bidLevelList)
        .build();
  }

  /**
   * Prints the depth.
   */
  private void showCasePrint() {
    final OrderBookData data = this.getDepth();
    final int printingDepth = 10;
    if (data.getAsks().size() >= printingDepth
        && data.getBids().size() >= printingDepth) {
      List<PriceLevel> askLevels = data.getAsks().subList(0, printingDepth);
      Collections.reverse(askLevels);
      List<PriceLevel> bidLevels = data.getBids().subList(0, printingDepth);

      System.out.println("----------------------------");
      askLevels.forEach(x -> {
        String priceString = Double.toString(x.getPrice());
        String amountString = Double.toString(x.getAmount());
        System.out.println("ask" + ": " + priceString + " ------ " + amountString);
        // System.out.println("ask" + ": " + x.getPrice().toPlainString()
        //     + " ------ " + x.getAmount().toPlainString());
      });
      System.out.println("   ");
      bidLevels.forEach(x -> {
        String priceString = Double.toString(x.getPrice());
        String amountString = Double.toString(x.getAmount());
        System.out.println("bid" + ": " + priceString + " ------ " + amountString);
        // System.out.println("bid" + ": " + x.getPrice().toPlainString()
        //     + " ------ " + x.getAmount().toPlainString());
      });
      System.out.println("----------------------------");
    }
  }
}
