package gt.trading.openbook.models;

/**
 * The DepthData class represents depth data information for a particular
 * instrument on the Huobi exchange. Depth data provides information about the
 * best ask and bid prices and sizes, as well as the quote time and sequence ID.
 */
public final class DepthData {
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
    return seqId;
  }

  /**
   * Retrieves the best ask price.
   *
   * @return the ask price
   */
  public double getAsk() {
    return ask;
  }

  /**
   * Retrieves the size of the best ask price.
   *
   * @return the ask size
   */
  public double getAskSize() {
    return askSize;
  }

  /**
   * Retrieves the best bid price.
   *
   * @return the bid price
   */
  public double getBid() {
    return bid;
  }

  /**
   * Retrieves the size of the best bid price.
   *
   * @return the bid size
   */
  public double getBidSize() {
    return bidSize;
  }

  /**
   * Retrieves the quote time in milliseconds.
   *
   * @return the quote time
   */
  public long getQuoteTime() {
    return quoteTime;
  }

  /**
   * Retrieves the trading symbol, e.g., "btcusdt".
   *
   * @return the symbol
   */
  public String getSymbol() {
    return symbol;
  }
}
