package gt.trading.huobi.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The PriceLevel class represents a price level and its corresponding amount in
 * an order book.
 */
public final class PriceLevel {
  private double price;
  private double amount;

  /**
   * Constructor for PriceLevel. Parses a list of doubles representing the price
   * level and its corresponding amount.
   *
   * @param newPrice  the price level
   * @param newAmount the amount of the price level
   * @throws IllegalArgumentException if the list does not contain two elements
   */
  @JsonCreator
  private PriceLevel(final @JsonProperty("price") double newPrice,
      final @JsonProperty("amount") double newAmount)
      throws IllegalArgumentException {
    price = newPrice;
    amount = newAmount;
  }

  /**
   * Gets the price of the price level.
   *
   * @return the price
   */
  public double getPrice() {
    return price;
  }

  /**
   * Gets the amount corresponding to the price level.
   *
   * @return the amount
   */
  public double getAmount() {
    return amount;
  }

  /**
   * Creates a new builder for constructing PriceLevel objects.
   *
   * @return a new instance of the Builder class
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Builder class for constructing PriceLevel objects using the builder
   * pattern.
   */
  public static final class Builder {
    private double price;
    private double amount;

    /**
     * Constructs an empty Builder for PriceLevel objects.
     */
    private Builder() {
      return;
    }

    /**
     * Sets the price of the price level.
     *
     * @param newPrice the price
     * @return the current instance of the Builder class
     */
    public Builder price(final double newPrice) {
      price = newPrice;
      return this;
    }

    /**
     * Sets the amount corresponding to the price level.
     *
     * @param newAmount the amount
     * @return the current instance of the Builder class
     */
    public Builder amount(final double newAmount) {
      amount = newAmount;
      return this;
    }

    /**
     * Constructs a new instance of the PriceLevel class using the current
     * builder state.
     *
     * @return a new instance of the PriceLevel class
     */
    public PriceLevel build() {
      return new PriceLevel(price, amount);
    }
  }
}
