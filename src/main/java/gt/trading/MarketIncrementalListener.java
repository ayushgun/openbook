package gt.trading;

import java.util.Map;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

import okhttp3.WebSocket;

public class MarketIncrementalListener extends Listener {
  private final String subscrptionString = "market.btcusdt.mbp.400";
  private final Callback<MbpIncrementalData> callback;

  public MarketIncrementalListener(Callback<MbpIncrementalData> callback) {
    this.callback = callback;
  }

  @Override
  protected void subscribe(final WebSocket webSocket) {
    // Subscribe to BTC-USDT depth channel
    JSONObject subscribe = new JSONObject(
        Map.of("sub", subscrptionString, "id", "id1"));
    webSocket.send(subscribe.toJSONString());
  }

  @Override
  protected void handleEvent(String json) {
    try {
      JsonNode rootNode = objectMapper.readTree(json);
      if (rootNode.has("id") && "id2".equals(rootNode.get("id").asText())) {
        MbpIncrementalData data = objectMapper.treeToValue(rootNode.get("data"),
            MbpIncrementalData.class);
        data.setAction("REFRESH");
        this.callback.onResponse(data);
      } else if (rootNode.has("ch")
          && subscrptionString.equals(rootNode.get("ch").asText())) {
        if (rootNode.has("tick")) {
          MbpIncrementalData data = objectMapper
              .treeToValue(rootNode.get("tick"), MbpIncrementalData.class);
          data.setAction("INCREMENT");
          this.callback.onResponse(data);
        }
      } else if (rootNode.has("status")) {
        System.out.println("Status:" + json);
        this.requestRefresh();
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

  public void requestRefresh() {
    JSONObject subscribe = new JSONObject(
        Map.of("req", subscrptionString, "id", "id2"));
    webSocket.send(subscribe.toJSONString());
  }

}
