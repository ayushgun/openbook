package gt.trading;

public class TradeDetail {
  public TradeDetail(MarketListener listener) {
    listener.subscribeTradeDetail(data -> {
      System.out.println(data);
    });
  }
}
