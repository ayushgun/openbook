package gt.trading.openbook.featuregraph;

/**
 * Interface used to build feature graphs. Includes a build method that provides
 * custom logic to build a feature graph.
 */
public interface GraphBuilder {
  /**
   * Provides custom logic to build a feature graph containing certain features
   * within it.
   *
   * @param graph the FeatureGraph to build using custom logic
   */
  void build(FeatureGraph graph);
}
