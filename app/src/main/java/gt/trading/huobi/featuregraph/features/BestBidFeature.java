package gt.trading.huobi.featuregraph.features;

import gt.trading.huobi.featuregraph.FeatureGraph;
import gt.trading.huobi.featuregraph.Feature;
import gt.trading.huobi.buckets.DepthData;

public class BestBidFeature implements Feature {
  private Double bestBid = Double.NaN;
  private final String name = "BestBidFeature";

  public BestBidFeature(FeatureGraph featureGraph) {
    featureGraph.registerFeature(this, false);
    featureGraph.registerDepthEventCallback(this::onDepthEvent);
  }

  @Override
  public Boolean onDepthEvent(DepthData depthData) {
    if (Double.compare(depthData.getBestBid(), this.bestBid) != 0) {
      this.bestBid = depthData.getBestBid();
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }

  @Override
  public Double getValue() {
    return this.bestBid;
  }

  @Override
  public String getName() {
    return this.name;
  }

}