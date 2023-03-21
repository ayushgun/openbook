package gt.trading.huobi;

public abstract class InferenceNode extends FeatureNode {
  protected FeatureNode[] parents;
  protected final String[] parentNames;

  protected final int numParents;

  protected InferenceNode(String name, double initValue, String[] parentNames,
      int numParents) {
    super(name, initValue);
    this.parentNames = parentNames;
    this.numParents = numParents;
  }

  public void setParents(FeatureNode[] parents) {
    this.parents = parents;
  }

  protected abstract void onUpdate();
}
