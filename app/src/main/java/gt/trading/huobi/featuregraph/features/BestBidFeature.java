package gt.trading.huobi.featuregraph.features;

public class BestBidFeature implements Feature {
  private Double bestBid = Double.NaN;

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
    return "BestBidFeature";
  }

}