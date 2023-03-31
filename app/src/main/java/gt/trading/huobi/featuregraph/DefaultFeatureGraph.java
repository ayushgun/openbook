package gt.trading.huobi.featuregraph;

import java.util.function.Function;
import java.util.List;
import java.util.ArrayList;
import java.util.AbstractMap;

import gt.trading.huobi.buckets.DepthData;
import gt.trading.huobi.buckets.TradeData;
import gt.trading.huobi.buckets.OrderBookData;

import gt.trading.huobi.featuregraph.Feature;

public class DefaultFeatureGraph implements FeatureGraph {
  // private List<Function<DepthData, Boolean>> depthEventCallbacks = new
  // ArrayList<>();
  private List<AbstractMap.SimpleEntry<Function<DepthData, Boolean>, FeatureNode>> depthEventCallbacks = new ArrayList<>();
  // private List<Function<TradeData, Boolean>> tradeEventCallbacks = new
  // ArrayList<>();
  private List<AbstractMap.SimpleEntry<Function<TradeData, Boolean>, FeatureNode>> tradeEventCallbacks = new ArrayList<>();
  // private List<Function<OrderBookData, Boolean>> orderBookEventCallbacks =
  // new ArrayList<>();
  private List<AbstractMap.SimpleEntry<Function<OrderBookData, Boolean>, FeatureNode>> orderBookEventCallbacks = new ArrayList<>();

  private List<Feature> notProcessedFeatures = new ArrayList<>();
  private List<Feature> processedFeatures = new ArrayList<>();

  private Map<String, FeatureNode> featureNodes = new HashMap<>();

  private class FeatureNode {
    // private List<Function<Feature, Boolean>> childrenOnUpdates;
    private List<AbstractMap.SimpleEntry<Function<Feature, Boolean>, FeatureNode>> childrenOnUpdates;
    private Feature feature;

    public FeatureNode(Feature feature) {
      this.feature = feature;
    }

    public addChildren(Feature feature, Function<Feature, Boolean> onParentUpdate) {
      childrenOnUpdates.add(new AbstractMap.SimpleEntry<>(onParentUpdate, feature));
    }

    public onUpdate() {
      for (AbstractMap.SimpleEntry<Function<Feature, Boolean>, FeatureNode> entry: childrenOnUpdates) {
        Function<Feature, Boolean> childOnUpdate = entry.getKey();
        if (childOnUpdate(feature)) {
          FeatureNode childNode = entry.getValue();
          childNode.onUpdate();
        }
      }
    }

  }

  public void addParent(Feature feature, Feature parentFeature,
      Function<Feature, Boolean> onParentUpdate) {
    FeatureNode node = this.featureNodes.get(feature.toString());
    FeatureNode parentNode = this.featureNodes.get(parentFeature.toString());
    parentNode.addChildren(onParentUpdate);
  }

  public void registerFeature(Feature feature, boolean shouldProcess) {
    this.featureNodes.put(feature.toString(), new FeatureNode(feature));
    if (shouldProcess) {
      processedFeatures.add(feature);
    } else {
      notProcessedFeatures.add(feature);
    }
  }

  public void registerDepthEventCallback(Feature feature,
      Function<DepthData, Boolean> onDepthEvent) {
    depthEventCallbacks.add(new AbstractMap.SimpleEntry<>(onDepthEvent,
        featureNodes.get(feature.toString())));
  }

  public void registerTradeEventCallback(Feature feature,
      Function<TradeData, Boolean> onTradeEvent) {
    tradeEventCallbacks.add(new AbstractMap.SimpleEntry<>(onTradeEvent,
        featureNodes.get(feature.toString())));
  }

  public void registerOrderBookEventCallback(Feature feature,
      Function<OrderBookData, Boolean> onOrderBookEvent) {
    // orderBookEventCallbacks.add(onOrderBookEvent);
    orderBookEventCallbacks.add(new AbstractMap.SimpleEntry<>(onOrderBookEvent,
        featfeatureNodes.get(feature.toString())));
  }

  public Boolean onDepthEvent(DepthData depthData) {
    // AbstractMap.SimpleEntry<Function<DepthData, Boolean>, FeatureNode> entry:
    for (AbstractMap.SimpleEntry<Function<DepthData, Boolean>, FeatureNode> entry : depthEventCallbacks) {
      Function<DepthData, Boolean> callback = entry.getKey();
      if (callback.apply(depthData)) {
        FeatureNode node = entry.getValue();
        node.onUpdate();
      }
    }
    return Boolean.TRUE; // ! temporary
  }

  public Boolean onTradeEvent(TradeData tradeData) {
    for (AbstractMap.SimpleEntry<Function<TradeData, Boolean>, FeatureNode> entry : tradeEventCallbacks) {
      Function<TradeData, Boolean> callback = entry.getKey();
      if (callback.apply(tradeData)) {
        FeatureNode node = entry.getValue();
        node.onUpdate();
      }
    }
    return Boolean.TRUE; // ! temporary
  }

  public Boolean onOrderBookEvent(OrderBookData orderBookData) {
    for (AbstractMap.SimpleEntry<Function<OrderBookData, Boolean>, FeatureNode> entry : orderBookEventCallbacks) {
      Function<OrderBookData, Boolean> callback = entry.getKey();
      if (callback.apply(orderBookData)) {
        FeatureNode node = entry.getValue();
        node.onUpdate();
      }
    }
    return Boolean.TRUE; // ! temporary
  }
}
