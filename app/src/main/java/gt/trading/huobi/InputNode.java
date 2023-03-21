package gt.trading.huobi;

public abstract class InputNode extends FeatureNode {
  protected InputNode(String name, double initValue) {
    super(name, initValue);
  }

  protected void onUpdate(double value) {
    this.value = value;
  }
}
