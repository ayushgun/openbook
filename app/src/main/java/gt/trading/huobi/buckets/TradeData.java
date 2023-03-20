package gt.trading.huobi.buckets;

public class TradeData {
  private String id;
  private long ts;
  private long tradeId;
  private double amount;
  private double price;
  private String direction;

  public String getId() {
    return this.id;
  }

  public long getTs() {
    return this.ts;
  }

  public long getTradeId() {
    return this.tradeId;
  }

  public double getAmount() {
    return this.amount;
  }

  public double getPrice() {
    return this.price;
  }

  public String getDirection() {
    return this.direction;
  }

}
