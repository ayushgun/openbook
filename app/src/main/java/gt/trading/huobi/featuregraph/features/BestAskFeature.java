package gt.trading.huobi.featuregraph.features;

public class BestAskFeature implements Feature {
  private Double bestAsk = Double.NaN;

  public BestAskFeature(FeatureGraph featureGraph) {
    featureGraph.registerFeature(this, false);
    featureGraph.registerDepthEventCallback(this::onDepthEvent);
  }

  @Override
  public Boolean onDepthEvent(DepthData depthData) {
    if (Double.compare(depthData.getBestAsk(), this.bestAsk) != 0) {
      this.bestAsk = depthData.getBestAsk();
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }

  @Override
  public Double getValue() {
    return this.bestAsk;
  }

  @Override
  public String getName() {
    return "BestAskFeature";
  }

}