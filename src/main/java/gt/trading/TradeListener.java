package gt.trading;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;

import okhttp3.WebSocket;

public class TradeListener extends Listener{
  private final String subscriptionString = "market.btcusdt.trade.detail";
  private final Callback<TradeData> callback;

  public TradeListener(Callback<TradeData> callback) {
    this.callback = callback;
  }

  /**
   * Subscribes to the proper Trade Data websocket.
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
   * Custom event handler for trade data.
   * 
   * @param json  json object to parse for data
   */
  @Override
  protected void handleEvent(String json) {
    try {
      JsonNode rootNode = objectMapper.readTree(json);
      
      if (rootNode.has("ch")
          && subscriptionString.equals(rootNode.get("ch").asText())) {
        if (rootNode.has("tick")) {
          TradeData[] data = objectMapper
              .treeToValue(rootNode.get("tick").get("data"), TradeData[].class);
          this.callback.onResponse(data[0]);
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
      e.printStackTrace();
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

}
