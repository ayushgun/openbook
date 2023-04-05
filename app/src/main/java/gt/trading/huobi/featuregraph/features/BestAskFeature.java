package gt.trading.huobi.featuregraph.features;

import gt.trading.huobi.featuregraph.FeatureGraph;
import gt.trading.huobi.featuregraph.Feature;
import gt.trading.huobi.buckets.DepthData;

public class BestAskFeature implements Feature {
  private Double value = Double.NaN;
  private final String name = "BestAskFeature";

  public BestAskFeature(FeatureGraph featureGraph) {
    featureGraph.registerFeature(this, true);
    featureGraph.registerDepthEventCallback(this, this::onDepthEvent);
  }

  @Override
  public boolean onDepthEvent(DepthData depthData) {
    if (Double.compare(depthData.getBestAsk(), this.value) != 0) {
      this.value = depthData.getBestAsk();
      return true;
    }
    return false;
  }

  @Override
  public Double getValue() {
    return this.value;
  }

  @Override
  public String toString() {
    return this.name;
  }

}
