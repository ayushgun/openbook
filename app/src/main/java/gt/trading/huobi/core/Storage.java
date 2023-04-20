package gt.trading.huobi.core;

import com.fasterxml.jackson.databind.ObjectMapper;

import gt.trading.huobi.listeners.MarketListener;

public class Storage {

  /**
   * Hello.
   */
  public Storage() {
    MarketListener listener = new MarketListener();
    listener.connect("wss://api-aws.huobi.pro/ws");
    ObjectMapper objectMapper = new ObjectMapper();
    listener.subscribeDepth(data -> {
      try {
        String jsonString = objectMapper.writeValueAsString(data);
        System.out.println(jsonString);
      } catch (Exception e) {
        System.out.println(e);
      }
    });
  }

  /**
   * Heloo.
   *
   * @param data
   */
  public void onDepthEvent(final TradeData data) {
    System.out.println(jsonString);
  }
}
