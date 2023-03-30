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
  private double bestAsk;
  private double bestAskSize;
  private double bestBid;
  private double bestBidSize;
  private long quoteTime;
  private String symbol;

  public long getSeqId() {
    return this.seqId;
  }

  public double getBestAsk() {
    return this.ask;
  }

  public double getBestAskSize() {
    return this.askSize;
  }

  public double getBestBid() {
    return this.bid;
  }

  public double getBestBidSize() {
    return this.bidSize;
  }

  public long getQuoteTime() {
    return this.quoteTime;
  }

  public String getSymbol() {
    return this.symbol;
  }

}
