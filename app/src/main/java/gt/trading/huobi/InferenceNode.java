package gt.trading.huobi;

public abstract class InferenceNode extends FeatureNode {
  protected FeatureNode[] parents;
  protected String[] parentNames;

  protected int numParents;

  protected InferenceNode(String name, double initValue) {
    super(name, initValue);
  }

  public void setParents(FeatureNode[] parents) {
    this.parents = parents;
  }

  protected abstract void onUpdate();
}
