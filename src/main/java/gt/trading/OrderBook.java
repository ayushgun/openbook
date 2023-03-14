package gt.trading;

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

public class OrderBook {
  private volatile LinkedBlockingQueue<MbpIncrementalData> UPDATE_QUEUE = new LinkedBlockingQueue<>();

  private volatile Map<BigDecimal, BigDecimal> BIDS_MAP = new TreeMap<>(
      Comparator.reverseOrder());

  private volatile Map<BigDecimal, BigDecimal> ASKS_MAP = new TreeMap<>();

  // private String symbol;

  private Long lastSeqNum = -1L;

  private boolean isFirst = true;

  private MarketIncrementalListener listener;

  public OrderBook() {
    listener = new MarketIncrementalListener(data -> {

      if (isFirst) {
        listener.requestRefresh();
        isFirst = false;
      }

      incrementUpdateTask(data);
      showCasePrint();

    });
    listener.createWebSocketConnection("wss://api.huobi.pro/feed");
  }

  private void incrementUpdateTask(MbpIncrementalData data) {

    if ("REFRESH".equals(data.getAction())) {
      // 全量请求对齐
      Long snapshotSeqNum = data.getSeqNum();

      List<MbpIncrementalData> preUpdateList = new ArrayList<>(
          UPDATE_QUEUE.size());
      // 把队列里的数据拿出来
      UPDATE_QUEUE.drainTo(preUpdateList);

      boolean isFinish = false;
      int index = 0;
      for (MbpIncrementalData preData : preUpdateList) {
        index++;

        Long preSeqNum = preData.getPrevSeqNum();

        System.out.println("data:::: preSeqNum:" + preSeqNum + "  seqNum:"
            + preData.getSeqNum() + "  snapshot:" + snapshotSeqNum);

        // 匹配成功
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

        // pre < seq 则忽略
        if (preSeqNum.compareTo(snapshotSeqNum) < 0) {
          System.out
              .println(" ignore message: preSeqNum:" + preSeqNum + "  seqNum:"
                  + preData.getSeqNum() + "  snapshot:" + snapshotSeqNum);
          continue;
        }

        // 如果出现pre > seq 则认为是漏消息了
        if (preSeqNum.compareTo(snapshotSeqNum) > 0) {
          System.out.println(
              "find incr message preSeqNum > snapshot seqNum....    message:"
                  + preSeqNum + "   snapshot:" + snapshotSeqNum);
          break;
        }

      }

      // 没有结束匹配，则出去
      if (!isFinish) {
        listener.requestRefresh();
        return;
      }

      for (int i = index; i < preUpdateList.size(); i++) {
        MbpIncrementalData preData = preUpdateList.get(i);
        incrementUpdate(preData);
      }

    } else {
      // 如果上一个lastSeqNum小于0，则认为是未初始化，先放到队列里
      if (lastSeqNum < 0) {
        UPDATE_QUEUE.add(data);
        return;
      }

      // 增量更新
      incrementUpdate(data);
    }

  }

  private void incrementUpdate(MbpIncrementalData data) {

    // 当前消息的pre 大于最后一次的seq 说明漏消息了。
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
    MbpIncrementalData data = this.getDepth();
    if (data.getAsks().size() >= 5 && data.getBids().size() >= 5) {
      List<PriceLevel> askLevels = data.getAsks().subList(0, 5);
      Collections.reverse(askLevels);
      List<PriceLevel> bidLevels = data.getBids().subList(0, 5);

      System.out.println("----------------------------");
      askLevels.forEach(x -> {
        System.out.println("ask" + ": " + x.getPrice().toPlainString()
            + " ------ " + x.getAmount().toPlainString());
      });
      bidLevels.forEach(x -> {
        System.out.println("bid" + ": " + x.getPrice().toPlainString()
            + " ------ " + x.getAmount().toPlainString());
      });
      System.out.println("----------------------------");
    }
  }
}
