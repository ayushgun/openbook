package gt.trading.openbook.featuregraph.features;

import gt.trading.openbook.featuregraph.FeatureGraph;
import gt.trading.openbook.featuregraph.Feature;

public final class MidPriceFeature implements Feature {
  private Double bestAsk;
  private Double bestBid;
  private Double value = Double.NaN;
  private final String name = "MidPriceFeature";

  public MidPriceFeature(final FeatureGraph featureGraph,
      final BestAskFeature bestAskFeature,
      final BestBidFeature bestBidFeature) {
    featureGraph.registerFeature(this, true);
    featureGraph.addParent(this, bestAskFeature, this::onBestAskUpdate);
    featureGraph.addParent(this, bestBidFeature, this::onBestBidUpdate);

    assert bestAskFeature.toString() == "BestAskFeature";
    this.bestAsk = bestAskFeature.getValue();
    assert bestBidFeature.toString() == "BestBidFeature";
    this.bestBid = bestBidFeature.getValue();
  }

  public boolean onBestAskUpdate(final Feature bestAskFeature) {
    if (Double.compare(bestAskFeature.getValue(), this.bestAsk) != 0) {
      this.bestAsk = bestAskFeature.getValue();
      return true;
    }
    return false;
  }

  public boolean onBestBidUpdate(final Feature bestBidFeature) {
    if (Double.compare(bestBidFeature.getValue(), this.bestBid) != 0) {
      this.bestAsk = bestBidFeature.getValue();
      return true;
    }
    return false;
  }

  @Override
  public void update() {
    this.value = (this.bestAsk + this.bestBid) / 2;
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
