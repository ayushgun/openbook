package gt.trading.huobi;

public abstract class FeatureNode {
  final String name;
  double value;
  private FeatureNode[] parents;
  protected String[] parentNames;

  protected final int numParents;

  protected FeatureNode() {

  };

  public updateParents(FeatureNode[] parents) {
    this.parents = parents;
  }

  protected abstract void onUpdate();
}
