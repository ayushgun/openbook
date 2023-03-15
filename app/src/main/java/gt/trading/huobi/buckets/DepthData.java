package gt.trading.huobi.buckets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DepthData {
  private Long seqId;
  private Double ask;
  private Double askSize;
  private Double bid;
  private Double bidSize;
  private Long quoteTime;
  private String symbol;
}
