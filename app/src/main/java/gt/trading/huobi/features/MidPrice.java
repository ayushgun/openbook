package gt.trading.huobi.features;

import gt.trading.huobi.InferenceNode;

public class MidPrice extends InferenceNode {
  public MidPrice() {
    super("midPrice", 0, new String[] {"bestBid", "bestAsk"}, 2);
  }

  protected void onUpdate() {
    double bestBid = parents[0].value;
    double bestAsk = parents[1].value;

    value = (bestBid + bestAsk) / 2;
  }
}
