package gt.trading.openbook.featuregraph.features;

import gt.trading.openbook.featuregraph.FeatureGraph;
import gt.trading.openbook.featuregraph.Feature;
import gt.trading.openbook.models.DepthData;

public final class BestAskFeature implements Feature {
  private Double value = Double.NaN;
  private final String name = "BestAskFeature";

  /**
   * Constructs a new bestAskFeature, registers the feature into the graph, and
   * invokes the callback function to ensure it is registered within the graph.
   *
   * @param featureGraph
   */
  public BestAskFeature(final FeatureGraph featureGraph) {
    featureGraph.registerFeature(this, true);
    featureGraph.registerDepthEventCallback(this, this::onDepthEvent);
  }

  @Override
  public boolean onDepthEvent(final DepthData depthData) {
    if (Double.compare(depthData.getAsk(), this.value) != 0) {
      this.value = depthData.getAsk();
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
