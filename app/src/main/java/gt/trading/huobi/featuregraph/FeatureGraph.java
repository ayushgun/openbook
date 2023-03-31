package gt.trading.huobi.featuregraph;

import java.util.function.Function;
import gt.trading.huobi.buckets.DepthData;
import gt.trading.huobi.buckets.TradeData;
import gt.trading.huobi.buckets.OrderBookData;

public interface FeatureGraph {
  public void registerFeature(Feature feature, boolean shouldProcess);

  public void addParent(Feature feature, Feature parentFeature,
      Function<Feature, Boolean> onParentUpdate);

  public void registerDepthEventCallback(Feature feature,
      Function<DepthData, Boolean> onDepthEvent);

  public void registerTradeEventCallback(Feature feature,
      Function<TradeData, Boolean> onTradeEvent);

  public void registerOrderBookEventCallback(Feature feature,
      Function<OrderBookData, Boolean> onOrderBookEvent);

  public Boolean onDepthEvent(DepthData depthData);

  public Boolean onTradeEvent(TradeData tradeData);

  public Boolean onOrderBookEvent(OrderBookData orderBookData);

  public void printValues();

}
