package gt.trading.huobi;

import java.util.ArrayList;

import gt.trading.huobi.buckets.TradeData;
import gt.trading.huobi.listeners.MarketListener;

public class TradeEvent {
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
