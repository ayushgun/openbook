package gt.trading.huobi;

import gt.trading.huobi.buckets.TradeData;
import gt.trading.huobi.listeners.MarketListener;

import java.util.ArrayList;

public class TradeEvent {
  
  /**
   * Trade event constructor.
   * 
   * @param listener  MarketListener object
   */
  public TradeEvent(MarketListener listener) {
    ArrayList<TradeData> list = new ArrayList<>();
    listener.subscribeTradeDetail(data -> {
      if (list.size() <= 1000) {
        list.add(data);
      }
      System.out.println(data);
    });
  }
}
