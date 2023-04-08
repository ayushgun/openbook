package gt.trading.huobi.models;

/**
 * TradeData class represents trade detail information for a particular
 * instrument on the Huobi exchange. Trade detail data provides information
 * about the trade ID, amount, price, and order book direction.
 */
public class TradeData {
  private String id;
  private long ts;
  private long tradeId;
  private double amount;
  private double price;
  private String direction;

  /**
   * Gets the ID of the trade data event.
   *
   * @return the ID of the event
   */
  public String getId() {
    return this.id;
  }

  /**
   * Gets the timestamp of the trade data event.
   *
   * @return the timestamp of the event
   */
  public long getTs() {
    return this.ts;
  }

  /**
   * Gets the unique trade ID of the trade data.
   *
   * @return the trade ID
   */
  public long getTradeId() {
    return this.tradeId;
  }

  /**
   * Gets the amount of the trade.
   *
   * @return the amount of the trade
   */
  public double getAmount() {
    return this.amount;
  }

  /**
   * Gets the price of the trade.
   *
   * @return the price of the trade
   */
  public double getPrice() {
    return this.price;
  }

  /**
   * Gets the direction of the trade (taker).
   *
   * @return the direction of the trade ('buy' or 'sell')
   */
  public String getDirection() {
    return this.direction;
  }
}
