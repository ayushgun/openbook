package gt.trading.huobi.buckets;

public class DepthData {
  private long seqId;
  private double ask;
  private double askSize;
  private double bid;
  private double bidSize;
  private long quoteTime;
  private String symbol;

  public long getSeqId() {
    return this.seqId;
  }

  public double getAsk() {
    return this.ask;
  }

  public double getAskSize() {
    return this.askSize;
  }

  public double getBid() {
    return this.bid;
  }

  public double getBidSize() {
    return this.bidSize;
  }

  public long getQuoteTime() {
    return this.quoteTime;
  }

  public String getSymbol() {
    return this.symbol;
  }

}
