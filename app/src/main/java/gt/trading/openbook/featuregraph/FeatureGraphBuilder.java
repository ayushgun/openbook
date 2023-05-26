package gt.trading.openbook.featuregraph;

/**
 * Interface used to build feature graphs.
 * Includes a build method that provides custom logic to build a feature graph.
 */
public interface FeatureGraphBuilder {
  /**
   * Provides custom logic to build a feature graph containing
   * certain features within it.
   *
   * @param graph the graph used to build the feature graph.
   */
  void build(FeatureGraph graph);
}
