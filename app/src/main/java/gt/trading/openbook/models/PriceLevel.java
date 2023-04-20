package gt.trading.openbook.models;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;

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
   * @param values the price and amount
   * @throws IllegalArgumentException if the list does not contain two elements
   */
  @JsonCreator
  private PriceLevel(final List<Double> values)
      throws IllegalArgumentException {
    price = values.get(0);
    amount = values.get(1);
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
      if (newPrice < 0) {
        throw new IllegalArgumentException(
            "Price of the price level cannot be less than 0");
      }

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
      if (amount < 0) {
        throw new IllegalArgumentException(
            "Amount of the price level cannot be less than 0");
      }

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
      return new PriceLevel(Arrays.asList(price, amount));
    }
  }
}
