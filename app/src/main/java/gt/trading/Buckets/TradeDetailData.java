package gt.trading.buckets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TradeDetailData {

  private String id;

  private Long ts;

  private Long tradeId;

  private Double amount;

  private Double price;

  private String direction;

}
