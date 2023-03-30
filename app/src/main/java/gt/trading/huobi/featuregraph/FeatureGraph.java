package gt.trading.huobi.featuregraph;

public interface FeatureGraph {
  public void registerFeature(Feature feature, boolean shouldProcess);

  public void registerDepthEventCallback(
      Function<DepthData, Boolean> onDepthEvent);

  public void registerTradeEventCallback(
      Function<TradeData, Boolean> onTradeEvent);

  public void registerOrderBookEventCallback(
      Function<OrderBookData, Boolean> onOrderBookEvent);

  public Boolean onDepthEvent(DepthData depthData);

  public Boolean onTradeEvent(TradeData tradeData);

  public Boolean onOrderBookEvent(OrderBookData orderBookData);

}
