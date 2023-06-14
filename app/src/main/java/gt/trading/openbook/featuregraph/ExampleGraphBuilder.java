package gt.trading.openbook.featuregraph;

import gt.trading.openbook.featuregraph.features.BestAskFeature;
import gt.trading.openbook.featuregraph.features.BestBidFeature;
import gt.trading.openbook.featuregraph.features.MidPriceFeature;

/**
 * Example implementation of how to build a feature graph using the
 * FeatureGraphBuilder interface.
 */
public class ExampleGraphBuilder implements GraphBuilder {
  /**
   * Constructs a feature graph by instantiating the features to be added to the
   * graph.
   *
   * @param graph The FeatureGraph that will be used to register features.
   */
  public void build(final FeatureGraph graph) {
    BestAskFeature bestAskFeature = new BestAskFeature(graph);
    BestBidFeature bestBidFeature = new BestBidFeature(graph);
    MidPriceFeature midPriceFeature = new MidPriceFeature(graph, bestAskFeature,
        bestBidFeature);
  }
}
