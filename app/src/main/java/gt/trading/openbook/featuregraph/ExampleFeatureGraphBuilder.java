package gt.trading.openbook.featuregraph;

import gt.trading.openbook.featuregraph.features.BestAskFeature;
import gt.trading.openbook.featuregraph.features.BestBidFeature;
import gt.trading.openbook.featuregraph.features.MidPriceFeature;

public class ExampleFeatureGraphBuilder implements FeatureGraphBuilder {
  public void build(FeatureGraph graph) {
    BestAskFeature bestAskFeature = new BestAskFeature(graph);
    BestBidFeature bestBidFeature = new BestBidFeature(graph);
    MidPriceFeature midPriceFeature = new MidPriceFeature(graph, bestAskFeature,
      bestBidFeature);
  }
}
