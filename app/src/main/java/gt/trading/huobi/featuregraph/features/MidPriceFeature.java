package gt.trading.huobi.featuregraph.features;

public class MidPriceFeature implements Feature {
  private BestAskFeature bestAskfeature;
  private BestBidFeature bestBidFeature;

  public MidPriceFeature(FeatureGraph featureGraph,
      BestAskFeature bestAskfeature, BestBidFeature bestBidFeature) {
    featureGraph.registerFeature(this, true);
    this.bestAskfeature = bestAskfeature;
    this.bestBidFeature = bestBidFeature;
  }

  @Override
  public Double getValue() {
    return (bestAskfeature.getValue() + bestBidfeature.getValue()) / 2;
  }

  @Override
  public String getName() {
    return "MidPriceFeature";
  }
}