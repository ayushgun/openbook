package gt.trading.huobi.featuregraph;

import java.util.function.Function;
import java.util.List;
import java.util.ArrayList;

import gt.trading.huobi.buckets.DepthData;
import gt.trading.huobi.buckets.TradeData;
import gt.trading.huobi.buckets.OrderBookData;

public class DefaultFeatureGraph implements FeatureGraph {
  private List<Function<DepthData, Boolean>> depthEventCallbacks = new ArrayList<>();
  private List<Function<TradeData, Boolean>> tradeEventCallbacks = new ArrayList<>();
  private List<Function<OrderBookData, Boolean>> orderBookEventCallbacks = new ArrayList<>();

  private List<Feature> notProcessedFeatures = new ArrayList<>();
  private List<Feature> processedFeatures = new ArrayList<>();

  public void registerFeature(Feature feature, boolean shouldProcess) {
    if (shouldProcess) {
      processedFeatures.add(feature);
    } else {
      notProcessedFeatures.add(feature);
    }
  }

  public void registerDepthEventCallback(
      Function<DepthData, Boolean> onDepthEvent) {
    depthEventCallbacks.add(onDepthEvent);
  }

  public void registerTradeEventCallback(
      Function<TradeData, Boolean> onTradeEvent) {
    tradeEventCallbacks.add(onTradeEvent);
  }

  public void registerOrderBookEventCallback(
      Function<OrderBookData, Boolean> onOrderBookEvent) {
    orderBookEventCallbacks.add(onOrderBookEvent);
  }

  public Boolean onDepthEvent(DepthData depthData) {
    for (Function<DepthData, Boolean> callback : depthEventCallbacks) {
      callback.apply(depthData); // ignoring the return value
    }
    return Boolean.TRUE; // ! temporary
  }

  public Boolean onTradeEvent(TradeData tradeData) {
    for (Function<TradeData, Boolean> callback : tradeEventCallbacks) {
      callback.apply(tradeData); // ignoring the return value
    }
    return Boolean.TRUE; // ! temporary
  }

  public Boolean onOrderBookEvent(OrderBookData orderBookData) {
    for (Function<OrderBookData, Boolean> callback : orderBookEventCallbacks) {
      callback.apply(orderBookData); // ignoring the return value
    }
    return Boolean.TRUE; // ! temporary
  }
}
