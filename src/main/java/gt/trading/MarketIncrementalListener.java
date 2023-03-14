package gt.trading;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;

import okhttp3.WebSocket;

public class MarketIncrementalListener extends Listener {
  private final String subscriptionString = "market.btcusdt.mbp.400";
  private final Callback<MbpIncrementalData> callback;

  public MarketIncrementalListener(Callback<MbpIncrementalData> callback) {
    this.callback = callback;
  }

  /**
   * Subscribes to the proper websocket for MBPIncremental.
   * 
   * @param webSocket websocket to connect to
   */
  @Override
  protected void subscribe(final WebSocket webSocket) {
    // Subscribe to BTC-USDT depth channel
    JSONObject subscribe = new JSONObject(
        Map.of("sub", subscriptionString, "id", "id1"));
    webSocket.send(subscribe.toJSONString());
  }

  /**
   * Custom event handler for market data.
   * 
   * @param json json object to parse for data
   */
  @Override
  protected void handleEvent(String json) {
    try {
      JsonNode rootNode = objectMapper.readTree(json);

      if (rootNode.has("ch")
          && subscriptionString.equals(rootNode.get("ch").asText())) {
        if (rootNode.has("tick")) {
          MbpIncrementalData data = objectMapper
              .treeToValue(rootNode.get("tick"), MbpIncrementalData.class);
          this.callback.onResponse(data);
        }
      } else if (rootNode.has("status")) {
        System.out.println("Status:" + json);
      } else {
        System.out.println(json);
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

}
