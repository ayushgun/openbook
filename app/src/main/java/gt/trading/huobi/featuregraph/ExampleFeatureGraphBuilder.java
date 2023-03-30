package gt.trading.huobi.featuregraph;

import gt.trading.huobi.featuregraph.features.BestAskFeature;
import gt.trading.huobi.featuregraph.features.BestBidFeature;
import gt.trading.huobi.featuregraph.features.MidPriceFeature;

public class ExampleFeatureGraphBuilder implements FeatureGraphBuilder {
  public void build(FeatureGraph graph) {
    BestAskFeature bestAskFeature = new BestAskFeature(graph);
    BestBidFeature bestBidFeature = new BestBidFeature(graph);
    MidPriceFeature midPriceFeature = new MidPriceFeature(graph, bestAskFeature,
        bestBidFeature);
  }
}
