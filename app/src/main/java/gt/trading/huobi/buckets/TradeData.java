package gt.trading.huobi.buckets;

public class TradeData {
  private String id;
  private long ts;
  private long tradeId;
  private double amount;
  private double price;
  private String direction;

  /**
   * Gets ID.
   * 
   * @return  ID as string
   */
  public String getId() {
    return this.id;
  }

  /**
   * Gets timestamp.
   * 
   * @return timestamp as long
   */
  public long getTs() {
    return this.ts;
  }

  /**
   * Gets trade ID.
   * 
   * @return trade ID as long
   */
  public long getTradeId() {
    return this.tradeId;
  }

  /**
   * Gets amount.
   * 
   * @return amount as double
   */
  public double getAmount() {
    return this.amount;
  }

  /**
   * Gets price.
   * 
   * @return price as double
   */
  public double getPrice() {
    return this.price;
  }

  /**
   * Gets direction.
   * 
   * @return direction as string
   */
  public String getDirection() {
    return this.direction;
  }

}
