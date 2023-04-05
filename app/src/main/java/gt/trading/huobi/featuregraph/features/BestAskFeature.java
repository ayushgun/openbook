package gt.trading.huobi.featuregraph.features;

import gt.trading.huobi.featuregraph.FeatureGraph;
import gt.trading.huobi.featuregraph.Feature;
import gt.trading.huobi.buckets.DepthData;

public class BestAskFeature implements Feature {
  private Double bestAsk = Double.NaN;
  private final String name = "BestAskFeature";

  public BestAskFeature(FeatureGraph featureGraph) {
    featureGraph.registerFeature(this, false);
    featureGraph.registerDepthEventCallback(this, this::onDepthEvent);
  }

  @Override
  public boolean onDepthEvent(DepthData depthData) {
    if (Double.compare(depthData.getBestAsk(), this.bestAsk) != 0) {
      this.bestAsk = depthData.getBestAsk();
      return true;
    }
    return false;
  }

  @Override
  public Double getValue() {
    return this.bestAsk;
  }

  @Override
  public String toString() {
    return this.name;
  }

}
