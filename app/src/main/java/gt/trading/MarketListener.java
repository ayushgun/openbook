package gt.trading;

import java.util.Map;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

public class MarketListener extends Listener {
  private final String TRADE_DETAIL_SUB_STRING = "market.btcusdt.trade.detail";
  private final String BBO_SUB_STRING = "market.btcusdt.bbo";
  private Callback<TradeDetailData> tradeDetailCallback;
  private Callback<BboData> bboCallback;

  public void subscribeTradeDetail(Callback<TradeDetailData> callback) {
    // Subscribe to BTC-USDT depth channel
    this.tradeDetailCallback = callback;
    JSONObject subscribe = new JSONObject(
        Map.of("sub", TRADE_DETAIL_SUB_STRING, "id", "trade_detail"));
    sendIfOpen(subscribe.toJSONString());
  }

  public void subscribeBBO(Callback<BboData> callback) {
    // Subscribe to BTC-USDT depth channel
    this.bboCallback = callback;
    JSONObject subscribe = new JSONObject(
        Map.of("sub", BBO_SUB_STRING, "id", "bbo"));
    sendIfOpen(subscribe.toJSONString());
  }

  @Override
  protected void handleEvent(JsonNode rootNode) {
    try {
      if (rootNode.has("ch")) {
        if (TRADE_DETAIL_SUB_STRING.equals(rootNode.get("ch").asText())) {
          if (rootNode.has("tick")) {
            TradeDetailData[] data = objectMapper.treeToValue(
                rootNode.get("tick").get("data"), TradeDetailData[].class);
            this.tradeDetailCallback.onResponse(data[0]);
          }
        } else if (BBO_SUB_STRING.equals(rootNode.get("ch").asText())) {
          if (rootNode.has("tick")) {
            BboData data = objectMapper.treeToValue(rootNode.get("tick"),
                BboData.class);
            this.bboCallback.onResponse(data);
          }
        } else {
          throw new JsonProcessingException(
              "JSON data does not fit in any channel.") {
          };
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

}