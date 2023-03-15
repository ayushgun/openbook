package gt.trading;

public class Bbo {

  private Double bboBid;

  private Double bboAsk;

  private boolean isFirstBid = true;

  private boolean isFirstAsk = true;
  
  /**
   * Best Bid/Offer constructor.
   * 
   * @param listener  takes in a MarketListener object
   */
  public Bbo(MarketListener listener) {
    listener.subscribeBBO(data -> {
      if (isFirstAsk) {
        bboAsk = data.getAsk();
        isFirstAsk = false;
      } 
      if (isFirstBid) {
        bboBid = data.getBid();
        isFirstBid = false;
      }
      if (data.getAsk() > bboAsk) {
        bboAsk = data.getAsk();
      }
      if (data.getBid() > bboBid) {
        bboBid = data.getBid();
      }
      System.out.println(data);
    });
  }
}
