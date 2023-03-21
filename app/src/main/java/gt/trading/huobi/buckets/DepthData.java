package gt.trading.huobi.buckets;

public class DepthData {
  private long seqId;
  private double ask;
  private double askSize;
  private double bid;
  private double bidSize;
  private long quoteTime;
  private String symbol;

  /**
   * Gets sequence ID.
   * 
   * @return  sequence ID as long
   */
  public long getSeqId() {
    return this.seqId;
  }

  /**
   * Gets ask price.
   * 
   * @return  ask price as double
   */
  public double getAsk() {
    return this.ask;
  }

  /**
   * Gets ask size.
   * 
   * @return  ask size as double
   */
  public double getAskSize() {
    return this.askSize;
  }

  /**
   * Gets bid price.
   * 
   * @return  bid price as double
   */
  public double getBid() {
    return this.bid;
  }

  /**
   * Gets bid size.
   * 
   * @return  bid size as double
   */
  public double getBidSize() {
    return this.bidSize;
  }

  /**
   * Gets quote time.
   * 
   * @return  quote time as long
   */
  public long getQuoteTime() {
    return this.quoteTime;
  }

  /**
   * Gets symbol.
   * 
   * @return  symbol as string
   */
  public String getSymbol() {
    return this.symbol;
  }

}
