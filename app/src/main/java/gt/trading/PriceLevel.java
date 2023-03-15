package gt.trading;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.math.BigDecimal;
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

  private BigDecimal price;

  private BigDecimal amount;

  @JsonCreator
  public PriceLevel(List<BigDecimal> values) {
    if (values != null && values.size() == 2) {
      this.price = values.get(0);
      this.amount = values.get(1);
    } else {
      throw new IllegalArgumentException(
          "Invalid list size for PriceLevel object");
    }
  }

}
