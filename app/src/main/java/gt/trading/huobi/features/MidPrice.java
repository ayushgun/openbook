package gt.trading.huobi.features;

public class MidPrice extends FeatureNode {
  MidPrice() {
    parentNames = new String[] {"bestBid", "bestAsk"};
    numParents = 2;
    name = "midPrice";
  }

  protected void onUpdate() {
    double bestBid = parents[0];
    double bestAsk = parents[1];

    value = (bestBid + bestAsk) / 2;
  }
}
