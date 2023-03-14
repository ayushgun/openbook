package gt.trading;

public class OrderBook {
  private volatile LinkedBlockingQueue<MbpIncrementalUpdateEvent> UPDATE_QUEUE = new LinkedBlockingQueue<>();

  private volatile Map<BigDecimal, BigDecimal> BIDS_MAP = new TreeMap<>(
      Comparator.reverseOrder());

  private volatile Map<BigDecimal, BigDecimal> ASKS_MAP = new TreeMap<>();

  private String symbol;

  private MarketClient marketClient = MarketClient.create(new HuobiOptions());

  private Long lastSeqNum = -1L;

  private WebSocketConnection connection;

  private boolean isFirst = true;

  public OrderBook() {
    MarketIncrementalListener listener = new MarketIncrementalListener(data -> {
      System.out.println("Asks: " + data.getAsks().size() + ", Bids: "
          + data.getBids().size());
      // incrementUpdateTask(data);
    });
    listener.createWebSocketConnection("wss://api.huobi.pro/feed");
  }

}
