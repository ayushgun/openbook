package gt.trading.Buckets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BboData {

  private Long seqId;

  private Double ask;

  private Double askSize;

  private Double bid;

  private Double bidSize;

  private Long quoteTime;

  private String symbol;

}