package gt.trading.huobi.buckets;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderBookData {
  @JsonIgnore
  private String action;
  private long seqNum;
  private long prevSeqNum;
  private List<PriceLevel> bids;
  private List<PriceLevel> asks;
}
