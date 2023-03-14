package gt.trading;

import java.util.Map;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

import okhttp3.WebSocket;

public class MarketIncrementalListener extends Listener {
  private final String subscrptionString = "market.btcusdt.mbp.400";

  @Override
  public void subscribe(final WebSocket webSocket) {
    // Subscribe to BTC-USDT depth channel
    JSONObject subscribe = new JSONObject(
        Map.of("sub", subscrptionString, "id", "id1"));
    webSocket.send(subscribe.toJSONString());
  }

  @Override
  public void handleEvent(String json) {
    try {
      JsonNode rootNode = objectMapper.readTree(json);

      if (rootNode.has("ch")
          && subscrptionString.equals(rootNode.get("ch").asText())) {
        if (rootNode.has("tick")) {
          MbpIncrementEvent event = objectMapper
              .treeToValue(rootNode.get("tick"), MbpIncrementEvent.class);
          System.out.println("GGG:\n" + event);
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
