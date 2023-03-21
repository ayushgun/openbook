package gt.trading.huobi.features;

import gt.trading.huobi.InferenceNode;

public class SumDoubMidBestAsk extends InferenceNode {
  public SumDoubMidBestAsk() {
    super("sumDoubMidBestAsk", 0, new String[] {"doubleMidPrice", "bestAsk"},
        2);
  }

  protected void onUpdate() {
    double doubleMidPrice = parents[0].value;
    double bestAsk = parents[1].value;

    value = doubleMidPrice + bestAsk;
  }
}
