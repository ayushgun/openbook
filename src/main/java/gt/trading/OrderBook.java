package gt.trading;

import java.util.List;

import okhttp3.OkHttpClient;

public class OrderBook {
  private int count = 0;

  public OrderBook() {
    // OkHttpClient connection = Connection.connect("wss://api.huobi.pro/feed",
    // new MarketIncrementalListener(data -> {

    // System.out.println("GGG:\n" + data);
    // }));

    MarketIncrementalListener listener = new MarketIncrementalListener(data -> {
      // System.out.println("GGG:\n" + data);
      // this.count += 1;
      // System.out.println(this.count);
      // if (this.count == 20) {
      // listener.requestRefresh();
      // }
      System.out.println("Asks: " + data.getAsks().size() + ", Bids: "
          + data.getBids().size());
      // incrementUpdateTask(data);
    });
    listener.createWebSocketConnection("wss://api.huobi.pro/feed");
  }

  // private void incrementUpdateTask(MbpIncrementalData event) {

  // if (WebSocketConstants.ACTION_REP.equals(event.getAction())) {
  // // 全量请求对齐
  // Long snapshotSeqNum = event.getSeqNum();

  // List<MbpIncrementalData> preUpdateList = new ArrayList<>(
  // UPDATE_QUEUE.size());
  // // 把队列里的数据拿出来
  // UPDATE_QUEUE.drainTo(preUpdateList);

  // boolean isFinish = false;
  // int index = 0;
  // for (MbpIncrementalUpdateEvent eve : preUpdateList) {
  // index++;

  // Long preSeqNum = eve.getPrevSeqNum();

  // System.out.println("event:::: preSeqNum:" + preSeqNum + " seqNum:"
  // + eve.getSeqNum() + " snapshot:" + snapshotSeqNum);

  // // 匹配成功
  // if (preSeqNum.compareTo(snapshotSeqNum) == 0) {
  // event.getBids().forEach(priceLevel -> {
  // BIDS_MAP.put(priceLevel.getPrice(), priceLevel.getAmount());
  // });

  // event.getAsks().forEach(priceLevel -> {
  // ASKS_MAP.put(priceLevel.getPrice(), priceLevel.getAmount());
  // });

  // isFinish = true;
  // lastSeqNum = snapshotSeqNum;

  // incrementUpdate(eve);
  // System.out.println("....compare finish....");
  // break;
  // }

  // // pre < seq 则忽略
  // if (preSeqNum.compareTo(snapshotSeqNum) < 0) {
  // System.out.println(" ignore message: preSeqNum:" + preSeqNum
  // + " seqNum:" + eve.getSeqNum() + " snapshot:" + snapshotSeqNum);
  // continue;
  // }

  // // 如果出现pre > seq 则认为是漏消息了
  // if (preSeqNum.compareTo(snapshotSeqNum) > 0) {
  // System.out.println(
  // "find incr message preSeqNum > snapshot seqNum.... message:"
  // + preSeqNum + " snapshot:" + snapshotSeqNum);
  // break;
  // }

  // }

  // // 没有结束匹配，则出去
  // if (!isFinish) {
  // sendReqMessage();
  // return;
  // }

  // for (int i = index; i < preUpdateList.size(); i++) {
  // MbpIncrementalUpdateEvent eve = preUpdateList.get(i);
  // incrementUpdate(eve);
  // }

  // } else {
  // // 如果上一个lastSeqNum小于0，则认为是未初始化，先放到队列里
  // if (lastSeqNum < 0) {
  // UPDATE_QUEUE.add(event);
  // return;
  // }

  // // 增量更新
  // incrementUpdate(event);
  // }

  // }

  // private void incrementUpdate(MbpIncrementalUpdateEvent event) {

  // // 当前消息的pre 大于最后一次的seq 说明漏消息了。
  // if (event.getPrevSeqNum() > lastSeqNum) {
  // sendReqMessage();
  // System.out.println(" miss message ::: message:" + event.getPrevSeqNum()
  // + " snapshot:" + lastSeqNum);
  // lastSeqNum = -1L;
  // return;
  // }

  // if (event.getPrevSeqNum() < lastSeqNum) {
  // return;
  // }

  // lastSeqNum = event.getSeqNum();

  // if (event.getAsks() != null && event.getAsks().size() > 0) {
  // for (PriceLevel level : event.getAsks()) {
  // if (level.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
  // ASKS_MAP.remove(level.getPrice());
  // } else {
  // ASKS_MAP.put(level.getPrice(), level.getAmount());
  // }
  // }
  // }

  // if (event.getBids() != null && event.getBids().size() > 0) {
  // for (PriceLevel level : event.getBids()) {
  // if (level.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
  // BIDS_MAP.remove(level.getPrice());
  // } else {
  // BIDS_MAP.put(level.getPrice(), level.getAmount());
  // }
  // }
  // }

  // }
}
