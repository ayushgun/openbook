package gt.trading.openbook.featuregraph.features;

import gt.trading.openbook.featuregraph.FeatureGraph;
import gt.trading.openbook.featuregraph.Feature;
import gt.trading.openbook.models.DepthData;

public final class BestBidFeature implements Feature {
  private Double value = Double.NaN;
  private final String name = "BestBidFeature";

  public BestBidFeature(final FeatureGraph featureGraph) {
    featureGraph.registerFeature(this, true);
    featureGraph.registerDepthEventCallback(this, this::onDepthEvent);
  }

  @Override
  public boolean onDepthEvent(final DepthData depthData) {
    if (Double.compare(depthData.getBid(), this.value) != 0) {
      this.value = depthData.getBid();
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
