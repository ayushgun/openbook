package gt.trading.huobi.features;

import gt.trading.huobi.InferenceNode;

public class DoubleMidPrice extends InferenceNode {
  public DoubleMidPrice() {
    super("doubleMidPrice", 0, new String[] {"midPrice"}, 1);
  }

  protected void onUpdate() {
    double midPrice = parents[0].value;

    value = midPrice * 2;
  }
}
