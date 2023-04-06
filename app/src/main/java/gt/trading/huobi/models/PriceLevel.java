package gt.trading.huobi.models;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * PriceLevel class represents a price level and its corresponding amount in an
 * order book.
 */
public final class PriceLevel {
  private double price;
  private double amount;

  /**
   * Inner constructor for PriceLevel. Parses a list of doubles representing the
   * price level and its corresponding amount.
   *
   * @param values a list of doubles representing the price level and its amount
   * @throws IllegalArgumentException if the list does not contain two elements
   */
  @JsonCreator
  private PriceLevel(final List<Double> values)
      throws IllegalArgumentException {
    if (values == null || values.size() != 2) {
      throw new IllegalArgumentException(
          "invalid list size for PriceLevel object");
    }

    price = values.get(0);
    amount = values.get(1);
  }

  /**
   * Gets the price of the price level.
   *
   * @return the price as a double
   */
  public double getPrice() {
    return price;
  }

  /**
   * Gets the amount corresponding to the price level.
   *
   * @return the amount as a double
   */
  public double getAmount() {
    return amount;
  }

  /**
   * Creates a new builder for constructing PriceLevel objects.
   *
   * @return a new instance of the PriceLevel.Builder class
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
    private List<Double> values = new ArrayList<>();

    /**
     * Constructs an empty Builder for PriceLevel objects.
     */
    private Builder() {
      return;
    }

    /**
     * Sets the price of the price level.
     *
     * @param newPrice the price as a double
     * @return the current instance of the PriceLevel.Builder class
     */
    public Builder price(final double newPrice) {
      price = newPrice;
      return this;
    }

    /**
     * Sets the amount corresponding to the price level.
     *
     * @param newAmount the amount as a double
     * @return the current instance of the PriceLevel.Builder class
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
      values.add(price);
      values.add(amount);
      return new PriceLevel(values);
    }
  }
}
