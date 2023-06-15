package gt.trading.openbook.featuregraph.features;

import gt.trading.openbook.featuregraph.Feature;
import gt.trading.openbook.featuregraph.FeatureGraph;

public final class MidPriceFeature implements Feature {
  private Double bestAsk;
  private Double bestBid;
  private Double value = Double.NaN;
  private final String name = "MidPriceFeature";

  /**
   * Constructs a new MidPriceFeature instance, registers the feature into the
   * graph, adds bestAskFeature and bestBidFeature as parents and sets initial
   * bestAsk and bestBid values.
   *
   * @param featureGraph   the featureGraph to register the BestAskFeature to
   * @param bestAskFeature the bestAskFeature used to calculate MidPrice
   * @param bestBidFeature the bestBidFeature used to calculate MidPrice
   */
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

  /**
   * Compares the bestAskFeature and midPriceFeature's bestAsk values, then sets
   * the midPriceFeature's to the bestAskFeature's bestAsk value if they differ.
   *
   * @param bestAskFeature the bestAskFeature to get the value from
   * @return true if the value was changed, false otherwise
   */
  public boolean onBestAskUpdate(final Feature bestAskFeature) {
    if (Double.compare(bestAskFeature.getValue(), this.bestAsk) != 0) {
      this.bestAsk = bestAskFeature.getValue();
      return true;
    }

    return false;
  }

  /**
   * Compares the bestBidFeature and midPriceFeature's bestBid values, then sets
   * the midPriceFeature's to the bestBidFeature's bestBid value if they differ.
   *
   * @param bestBidFeature the bestBidFeature to get the value from
   * @return true if the value was changed, false otherwise
   */
  public boolean onBestBidUpdate(final Feature bestBidFeature) {
    if (Double.compare(bestBidFeature.getValue(), this.bestBid) != 0) {
      this.bestBid = bestBidFeature.getValue();
      return true;
    }

    return false;
  }

  /**
   * Updates the value of the feature.
   */
  @Override
  public void update() {
    this.value = (this.bestAsk + this.bestBid) / 2;
  }

  /**
   * Returns the value of the feature.
   */
  @Override
  public Double getValue() {
    return this.value;
  }

  /**
   * Returns the name of the feature.
   */
  @Override
  public String toString() {
    return this.name;
  }
}
