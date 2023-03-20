package gt.trading.huobi.buckets;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PriceLevel {
  private double price;
  private double amount;

  /**
   * Javadoc.
   * 
   * @param values  values
   */
  @JsonCreator
  public PriceLevel(final List<Double> values) {
    if (values == null || values.size() != 2) {
      throw new IllegalArgumentException(
          "Invalid list size for PriceLevel object");
    }

    this.price = values.get(0);
    this.amount = values.get(1);
  }

  public double getPrice() {
    return this.price;
  }

  public double getAmount() {
    return this.amount;
  }
  
}
