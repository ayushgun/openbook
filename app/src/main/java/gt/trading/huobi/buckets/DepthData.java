package gt.trading.huobi.buckets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DepthData {
  private long seqId;

  @JsonProperty("ask")
  private double bestAsk;

  @JsonProperty("askSize")
  private double bestAskSize;

  @JsonProperty("bid")
  private double bestBid;

  @JsonProperty("bidSize")
  private double bestBidSize;

  private long quoteTime;
  private String symbol;

  public long getSeqId() {
    return this.seqId;
  }

  public double getBestAsk() {
    return this.bestAsk;
  }

  public double getBestAskSize() {
    return this.bestAskSize;
  }

  public double getBestBid() {
    return this.bestBid;
  }

  public double getBestBidSize() {
    return this.bestBidSize;
  }

  public long getQuoteTime() {
    return this.quoteTime;
  }

  public String getSymbol() {
    return this.symbol;
  }

}
