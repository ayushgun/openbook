package gt.trading;

import java.awt.Color;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.JFrame;

public class OrderBook {
  private volatile LinkedBlockingQueue<MbpIncrementalData> UPDATE_QUEUE = new LinkedBlockingQueue<>();

  private volatile Map<BigDecimal, BigDecimal> BIDS_MAP = new TreeMap<>(
      Comparator.reverseOrder());

  private volatile Map<BigDecimal, BigDecimal> ASKS_MAP = new TreeMap<>();

  // private String symbol;

  private Long lastSeqNum = -1L;

  private boolean isFirst = true;

  private MarketIncrementalListener listener;

  private MarketListener listener2;

  /**
   * OrderBook constructor.
   */
  public OrderBook() {
    // JFrame frame = new JFrame();
    // frame.setTitle("OrderBook");
    // frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    // frame.setResizable(true);
    // frame.setSize(500, 500);
    // frame.setVisible(true);
    
    // frame.getContentPane().setBackground(Color.gray);

    listener = new MarketIncrementalListener();
    listener.createWebSocketConnection("wss://api.huobi.pro/feed");
    listener.subscribeMbpIncremental(data -> {

      if (isFirst) {
        listener.requestRefresh();
        isFirst = false;
      }

      incrementUpdateTask(data);
      showCasePrint();

    });
    listener2 = new MarketListener();
    listener2.createWebSocketConnection("wss://api.huobi.pro/ws");
    listener2.subscribeTradeDetail(null);
    listener2.subscribeBBO(null);
  }

  private void incrementUpdateTask(MbpIncrementalData data) {

    if ("REFRESH".equals(data.getAction())) {
      // Save the sequence number of this refresh event.
      Long snapshotSeqNum = data.getSeqNum();

      List<MbpIncrementalData> preUpdateList = new ArrayList<>(
          UPDATE_QUEUE.size());
      // Extract the updates that were saved in UPDATE_QUEUE.
      UPDATE_QUEUE.drainTo(preUpdateList);

      boolean isFinish = false;
      int index = 0;
      for (MbpIncrementalData preData : preUpdateList) {
        index++;

        Long preSeqNum = preData.getPrevSeqNum();

        System.out.println("data:::: preSeqNum:" + preSeqNum + "  seqNum:"
            + preData.getSeqNum() + "  snapshot:" + snapshotSeqNum);

        // Have updated all increments up to the refresh event, thus can break.
        if (preSeqNum.compareTo(snapshotSeqNum) == 0) {
          data.getBids().forEach(priceLevel -> {
            BIDS_MAP.put(priceLevel.getPrice(), priceLevel.getAmount());
          });

          data.getAsks().forEach(priceLevel -> {
            ASKS_MAP.put(priceLevel.getPrice(), priceLevel.getAmount());
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
        MbpIncrementalData preData = preUpdateList.get(i);
        incrementUpdate(preData);
      }

    } else {
      // if lastSeqNum < 0, it means we haven't refreshed yet, then we put it in
      // the queue first.
      if (lastSeqNum < 0) {
        UPDATE_QUEUE.add(data);
        return;
      }

      // Actual Incremental update.
      incrementUpdate(data);
    }

  }

  private void incrementUpdate(MbpIncrementalData data) {

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
        if (level.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
          ASKS_MAP.remove(level.getPrice());
        } else {
          ASKS_MAP.put(level.getPrice(), level.getAmount());
        }
      }
    }

    if (data.getBids() != null && data.getBids().size() > 0) {
      for (PriceLevel level : data.getBids()) {
        if (level.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
          BIDS_MAP.remove(level.getPrice());
        } else {
          BIDS_MAP.put(level.getPrice(), level.getAmount());
        }
      }
    }

  }

  /**
   * Need a javadoc comment.
   * 
   * @return
   */
  public MbpIncrementalData getDepth() {

    Iterator<Entry<BigDecimal, BigDecimal>> askIterator = ASKS_MAP.entrySet()
        .iterator();
    List<PriceLevel> askLevelList = new ArrayList<>();
    while (askIterator.hasNext()) {
      Entry<BigDecimal, BigDecimal> entry = askIterator.next();
      BigDecimal price = entry.getKey();
      BigDecimal amount = entry.getValue();
      askLevelList
          .add(PriceLevel.builder().amount(amount).price(price).build());
    }

    Iterator<Entry<BigDecimal, BigDecimal>> bidIterator = BIDS_MAP.entrySet()
        .iterator();
    List<PriceLevel> bidLevelList = new ArrayList<>();
    while (bidIterator.hasNext()) {
      Entry<BigDecimal, BigDecimal> entry = bidIterator.next();
      BigDecimal price = entry.getKey();
      BigDecimal amount = entry.getValue();
      bidLevelList
          .add(PriceLevel.builder().amount(amount).price(price).build());
    }

    return MbpIncrementalData.builder().asks(askLevelList).bids(bidLevelList)
        .build();
  }

  private void showCasePrint() {
    final MbpIncrementalData data = this.getDepth();
    final int PRINTING_DEPTH = 10;
    if (data.getAsks().size() >= PRINTING_DEPTH
        && data.getBids().size() >= PRINTING_DEPTH) {
      List<PriceLevel> askLevels = data.getAsks().subList(0, PRINTING_DEPTH);
      Collections.reverse(askLevels);
      List<PriceLevel> bidLevels = data.getBids().subList(0, PRINTING_DEPTH);

      System.out.println("----------------------------");
      askLevels.forEach(x -> {
        System.out.println("ask" + ": " + x.getPrice().toPlainString()
            + " ------ " + x.getAmount().toPlainString());
      });
      System.out.println("   ");
      bidLevels.forEach(x -> {
        System.out.println("bid" + ": " + x.getPrice().toPlainString()
            + " ------ " + x.getAmount().toPlainString());
      });
      System.out.println("----------------------------");
    }
  }
}