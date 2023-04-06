package gt.trading.huobi.models;

public class DepthData {
  private long seqId;
  private double ask;
  private double askSize;
  private double bid;
  private double bidSize;
  private long quoteTime;
  private String symbol;

  /**
   * Retrieves the sequence number uniquely identifying this depth data update.
   *
   * @return the sequence ID
   */
  public long getSeqId() {
    return this.seqId;
  }

  /**
   * Retrieves the best ask price.
   *
   * @return the ask price
   */
  public double getAsk() {
    return this.ask;
  }

  /**
   * Retrieves the size of the best ask price.
   *
   * @return the ask size
   */
  public double getAskSize() {
    return this.askSize;
  }

  /**
   * Retrieves the best bid price.
   *
   * @return the bid price
   */
  public double getBid() {
    return this.bid;
  }

  /**
   * Retrieves the size of the best bid price.
   *
   * @return the bid size
   */
  public double getBidSize() {
    return this.bidSize;
  }

  /**
   * Retrieves the quote time in milliseconds.
   *
   * @return the quote time
   */
  public long getQuoteTime() {
    return this.quoteTime;
  }

  /**
   * Retrieves the trading symbol, e.g., "btcusdt".
   *
   * @return the symbol
   */
  public String getSymbol() {
    return this.symbol;
  }
}
