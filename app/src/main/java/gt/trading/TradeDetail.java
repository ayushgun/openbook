package gt.trading;

import java.util.ArrayList;

public class TradeDetail {
  /**
   * Trade detail constructor.
   * 
   * @param listener  takes in a MarketListener object.
   */
  public TradeDetail(MarketListener listener) {
    ArrayList<TradeDetailData> list = new ArrayList<>();
    listener.subscribeTradeDetail(data -> {
      if (list.size() <= 1000) {
        list.add(data);
      }
      System.out.println(data);
    });
  }
}
