package gt.trading.huobi.buckets;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.ArrayList;
import java.util.List;

public class PriceLevel {
  private double price;
  private double amount;
 
  /**
   * Inner constructor for builder.
   * 
   * @param builder Builder object
   */
  @JsonCreator
  private PriceLevel(final List<Double> values) {
    if (values == null || values.size() != 2) {
      throw new IllegalArgumentException(
          "Invalid list size for PriceLevel object");
    }

    this.price = values.get(0);
    this.amount = values.get(1);
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
   * Gets amount.
   * 
   * @return amount as double
   */
  public double getAmount() {
    return this.amount;
  }

  /**
   * Gets builder.
   * 
   * @return builder object
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Builder class for PriceLevel.
   */
  public static class Builder {
    private double price;
    private double amount;
    private List<Double> values = new ArrayList<>();

    private Builder() {}

    /**
     * Sets price.
     * 
     * @param price  price as double
     * @return       builder object
     */
    public Builder price(final double price) {
      this.price = price;
      return this;
    }

    /**
     * Sets amount.
     * 
     * @param amount  amount as double
     * @return        builder object
     */
    public Builder amount(final double amount) {
      this.amount = amount;
      return this;
    }

    /**
     * Builds PriceLevel object.
     * 
     * @return PriceLevel object
     */
    public PriceLevel build() {
      values.add(price);
      values.add(amount);
      return new PriceLevel(values);
    }
  }

}
