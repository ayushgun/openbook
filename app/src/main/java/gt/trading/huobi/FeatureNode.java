package gt.trading.huobi;

public abstract class FeatureNode {
  public final String name;
  public double value;

  protected FeatureNode(String name, double initValue) {
    this.name = name;
    this.value = initValue;
  }
}
