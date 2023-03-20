package gt.trading.huobi;

public class FeatureGraph {
  private Map<String, FeatureNode> nameToNode = new HashMap<>();
  private FeatureNode[] updateFeatureOrder;

  public void register(FeatureNode node) {
    nameToNode.put(node.name, node);
  }

  public initialize() {
    // assign all parents, toposort
  }

  public void updateNodes() {
    for (FeatureNode node : updateFeatureOrder) {
      node.onUpdate();
    }
  }
}
