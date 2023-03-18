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
  private long seqId;
  private double ask;
  private double askSize;
  private double bid;
  private double bidSize;
  private long quoteTime;
  private String symbol;
}
