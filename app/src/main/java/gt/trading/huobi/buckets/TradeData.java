package gt.trading.huobi.buckets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TradeData {
  private String id;
  private long ts;
  private long tradeId;
  private double amount;
  private double price;
  private String direction;
}
