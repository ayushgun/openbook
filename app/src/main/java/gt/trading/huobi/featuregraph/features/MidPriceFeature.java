package gt.trading.huobi.featuregraph.features;

import gt.trading.huobi.featuregraph.FeatureGraph;
import gt.trading.huobi.featuregraph.Feature;

public class MidPriceFeature implements Feature {
  private Double bestAsk;
  private Double bestBid;
  private final String name = "MidPriceFeature";

  public MidPriceFeature(FeatureGraph featureGraph,
      BestAskFeature bestAskFeature, BestBidFeature bestBidFeature) {
    featureGraph.registerFeature(this, true);
    featureGraph.addParent(this, bestAskFeature, this::onBestAskUpdate);
    featureGraph.addParent(this, bestBidFeature, this::onBestBidUpdate);

    assert bestAskFeature.getName() == "BestAskFeature";
    this.bestAskFeature = bestAskFeature;
    assert bestBidFeature.getName() == "BestBidFeature";
    this.bestBidFeature = bestBidFeature;
  }

  public Boolean onBestAskUpdate(Feature bestAskFeature) {
    if (Double.compare(bestAskFeature.getValue(), this.bestAsk) != 0) {
      this.bestAsk = bestAskFeature.getValue();
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }

  public Boolean onBestBidUpdate(Feature bestBidFeature) {
    if (Double.compare(bestBidFeature.getValue(), this.bestBid) != 0) {
      this.bestBid = bestBidFeature.getValue();
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }

  @Override
  public Double getValue() {
    return (this.bestAsk + this.bestBid) / 2;
  }

  @Override
  public String toString() {
    return this.name;
  }
}