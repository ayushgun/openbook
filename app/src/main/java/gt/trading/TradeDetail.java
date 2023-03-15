package gt.trading;

import java.util.LinkedList;
import java.util.Queue;

public class TradeDetail {
  /**
   * Trade detail constructor.
   * 
   * @param listener  takes in a MarketListener object.
   */
  public TradeDetail(MarketListener listener) {
    Queue<TradeDetailData> queue = new LinkedList<>();
    listener.subscribeTradeDetail(data -> {
      if (queue.size() <= 1000) {
        queue.add(data);
      }
      System.out.println(data);
    });
  }
}
