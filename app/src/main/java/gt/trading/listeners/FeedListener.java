package gt.trading.listeners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import gt.trading.buckets.OrderBookData;

import java.util.Map;

public class FeedListener extends Listener {
  private final String subscrptionString = "market.btcusdt.mbp.400";
  private Callback<OrderBookData> callback;

  // public MarketIncrementalListener(Callback<MbpIncrementalData> callback) {
  // this.callback = callback;
  // }

  /**
   * Subscribes to the Market Incremental data channel.
   * 
   * @param callback callback function
   */
  public void subscribeMbpIncremental(Callback<OrderBookData> callback) {
    // Subscribe to BTC-USDT depth channel
    this.callback = callback;
    ObjectMapper mapper = new ObjectMapper();
    JsonNode subscribe = mapper
        .valueToTree(Map.of("sub", subscrptionString, "id", "id1"));
    sendIfOpen(subscribe.toString());
  }

  @Override
  protected void handleEvent(JsonNode rootNode) {
    try {
      if (rootNode.has("id") && "id2".equals(rootNode.get("id").asText())) {
        OrderBookData data = objectMapper.treeToValue(rootNode.get("data"),
            OrderBookData.class);
        data.setAction("REFRESH");
        this.callback.onResponse(data);
      } else if (rootNode.has("ch")
          && subscrptionString.equals(rootNode.get("ch").asText())) {
        if (rootNode.has("tick")) {
          OrderBookData data = objectMapper.treeToValue(rootNode.get("tick"),
              OrderBookData.class);
          data.setAction("INCREMENT");
          this.callback.onResponse(data);
        }
      } else if (rootNode.has("status")) {
        System.out.println("Status:" + rootNode);
      } else {
        System.out.println(rootNode);
        throw new JsonProcessingException(
            "JSON data does not fit in any category.") {
        };
      }
    } catch (JsonMappingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (JsonProcessingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Need a javadoc comment here.
   */
  public void requestRefresh() {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode request = mapper
        .valueToTree(Map.of("req", subscrptionString, "id", "id2"));
    sendIfOpen(request.toString());
  }
}
