package gt.trading.huobi.buckets;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public class OrderBookData {
  @JsonIgnore
  private String action;
  private long seqNum;
  private long prevSeqNum;
  private List<PriceLevel> bids;
  private List<PriceLevel> asks;
  
  /**
   * No args constructor for use in serialization.
   */
  public OrderBookData() {}

  /**
   * Inner constructor for builder.
   * 
   * @param builder Builder object
   */
  private OrderBookData(Builder builder) {
    this.action = builder.action;
    this.seqNum = builder.seqNum;
    this.prevSeqNum = builder.prevSeqNum;
    this.bids = builder.bids;
    this.asks = builder.asks;
  }

  /**
   * Gets action of the order book.
   * 
   * @return  action string
   */
  public String getAction() {
    return this.action;
  }

  /**
   * Sets action of the order book.
   * 
   * @param action  action string
   */
  public void setAction(String action) {
    this.action = action;
  }

  /**
   * Gets sequence number of the order book.
   * 
   * @return  sequence number
   */
  public long getSeqNum() {
    return this.seqNum;
  }

  /**
   * Sets sequence number of the order book.
   * 
   * @param seqNum  previous sequence number
   */
  public long getPrevSeqNum() {
    return this.prevSeqNum;
  }

  /**
   * Gets bids of the order book.
   * 
   * @return  list of bids
   */
  public List<PriceLevel> getBids() {
    return this.bids;
  }

  /**
   * Gets asks of the order book.
   * 
   * @return  list of asks
   */
  public List<PriceLevel> getAsks() {
    return this.asks;
  }
  
  /**
   * Builder for OrderBookData.
   * 
   * @return  Builder object
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Builder for OrderBookData.
   */
  public static class Builder {
    private String action;
    private long seqNum;
    private long prevSeqNum;
    private List<PriceLevel> bids;
    private List<PriceLevel> asks;

    /**
     * No args constructor for use in serialization.
     */
    private Builder() {
    }

    /**
     * Sets action of the order book.
     * 
     * @param action  action string
     * @return  Builder object
     */
    public Builder action(final String action) {
      this.action = action;
      return this;
    }

    /**
     * Sets sequence number of the order book.
     * 
     * @param seqNum  sequence number
     * @return  Builder object
     */
    public Builder seqNum(final long seqNum) {
      this.seqNum = seqNum;
      return this;
    }

    /**
     * Sets previous sequence number of the order book.
     * 
     * @param prevSeqNum  previous sequence number
     * @return  Builder object
     */
    public Builder prevSeqNum(final long prevSeqNum) {
      this.prevSeqNum = prevSeqNum;
      return this;
    }

    /**
     * Sets bids of the order book.
     * 
     * @param bids  list of bids
     * @return  Builder object
     */
    public Builder bids(final List<PriceLevel> bids) {
      this.bids = bids;
      return this;
    }

    /**
     * Sets asks of the order book.
     * 
     * @param asks  list of asks
     * @return  Builder object
     */
    public Builder asks(final List<PriceLevel> asks) {
      this.asks = asks;
      return this;
    }

    /**
     * Builds OrderBookData object.
     * 
     * @return  OrderBookData object
     */
    public OrderBookData build() {
      return new OrderBookData(this);
    }
  }

}
