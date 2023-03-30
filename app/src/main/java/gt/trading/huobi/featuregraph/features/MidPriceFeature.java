package gt.trading.huobi.featuregraph.features;

import gt.trading.huobi.featuregraph.FeatureGraph;
import gt.trading.huobi.featuregraph.Feature;

public class MidPriceFeature implements Feature {
  private BestAskFeature bestAskFeature;
  private BestBidFeature bestBidFeature;
  private final String name = "MidPriceFeature";

  public MidPriceFeature(FeatureGraph featureGraph,
      BestAskFeature bestAskFeature, BestBidFeature bestBidFeature) {
    featureGraph.registerFeature(this, true);
    assert bestAskFeature.getName() == "BestAskFeature";
    this.bestAskFeature = bestAskFeature;
    assert bestBidFeature.getName() == "BestBidFeature";
    this.bestBidFeature = bestBidFeature;
  }

  @Override
  public Double getValue() {
    return (this.bestAskFeature.getValue() + this.bestBidFeature.getValue())
        / 2;
  }

  @Override
  public String getName() {
    return this.name;
  }
}