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

  // private OrderBookData(Builder builder) {
  //   action = builder.action;
  //   seqNum = builder.seqNum;
  //   prevSeqNum = builder.prevSeqNum;
  //   bids = builder.bids;
  //   asks = builder.asks;
  // }

  public String getAction() {
    return this.action;
  }

  public long getSeqNum() {
    return this.seqNum;
  }

  public long getPrevSeqNum() {
    return this.prevSeqNum;
  }

  public List<PriceLevel> getBids() {
    return this.bids;
  }

  public List<PriceLevel> getAsks() {
    return this.asks;
  }

  // public static Builder builder() {
  //   return new Builder();
  // }

  // public static class Builder {
  //   private String action;
  //   private long seqNum;
  //   private long prevSeqNum;
  //   private List<PriceLevel> bids;
  //   private List<PriceLevel> asks;

  //   private Builder() {
  //   }

  //   public Builder action(final String action) {
  //     this.action = action;
  //     return this;
  //   }

  //   public Builder seqNum(final long seqNum) {
  //     this.seqNum = seqNum;
  //     return this;
  //   }

  //   public Builder prevSeqNum(final long prevSeqNum) {
  //     this.prevSeqNum = prevSeqNum;
  //     return this;
  //   }

  //   public Builder bids(final List<PriceLevel> bids) {
  //     this.bids = bids;
  //     return this;
  //   }

  //   public Builder asks(final List<PriceLevel> asks) {
  //     this.asks = asks;
  //     return this;
  //   }

  //   public OrderBookData build() {
  //     return new OrderBookData(this);
  //   }
  // }

}
