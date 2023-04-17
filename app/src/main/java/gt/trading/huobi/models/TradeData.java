package gt.trading.huobi.models;

/**
 * The TradeData class represents trade detail information for a particular
 * instrument on the Huobi exchange. Trade detail data provides information
 * about the trade ID, amount, price, and order book direction.
 */
public final class TradeData {
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
    return id;
  }

  /**
   * Gets the timestamp of the trade data event.
   *
   * @return the timestamp of the event
   */
  public long getTs() {
    return ts;
  }

  /**
   * Gets the unique trade ID of the trade data.
   *
   * @return the trade ID
   */
  public long getTradeId() {
    return tradeId;
  }

  /**
   * Gets the amount of the trade.
   *
   * @return the amount of the trade
   */
  public double getAmount() {
    return amount;
  }

  /**
   * Gets the price of the trade.
   *
   * @return the price of the trade
   */
  public double getPrice() {
    return price;
  }

  /**
   * Gets the direction of the trade (taker).
   *
   * @return the direction of the trade ('buy' or 'sell')
   */
  public String getDirection() {
    return direction;
  }
}
