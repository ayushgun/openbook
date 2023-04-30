package gt.trading.openbook.featuregraph;

import gt.trading.openbook.featuregraph.features.BestAskFeature;
import gt.trading.openbook.featuregraph.features.BestBidFeature;
import gt.trading.openbook.featuregraph.features.MidPriceFeature;

public class ExampleFeatureGraphBuilder implements FeatureGraphBuilder {
  /**
   * Constructs a feature graph by instantiatin the features to be added to
   * the graph.
   *
   * @param graph
   */
  public void build(final FeatureGraph graph) {
    BestAskFeature bestAskFeature = new BestAskFeature(graph);
    BestBidFeature bestBidFeature = new BestBidFeature(graph);
    MidPriceFeature midPriceFeature = new MidPriceFeature(graph, bestAskFeature,
      bestBidFeature);
  }
}
