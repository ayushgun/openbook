package gt.trading;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class MarketListener extends Listener {
  private final String tradeDetailString = "market.btcusdt.trade.detail";
  private final String bboString = "market.btcusdt.bbo";
  private Callback<TradeDetailData> tradeDetailCallback;
  private Callback<BboData> bboCallback;

  /**
   * Subscribes to trade details.
   * 
   * @param callback  callback function
   */
  public void subscribeTradeDetail(Callback<TradeDetailData> callback) {
    // Subscribe to BTC-USDT depth channel
    this.tradeDetailCallback = callback;
    ObjectMapper mapper = new ObjectMapper();
    JsonNode subscribe = mapper.valueToTree(Map.of("sub", tradeDetailString, "id", "trade_detail"));
    sendIfOpen(subscribe.toString());
  }

  /**
   * Subscribes to Best Bid/Offer.
   * 
   * @param callback  callback function
   */
  public void subscribeBBO(Callback<BboData> callback) {
    // Subscribe to BTC-USDT depth channel
    this.bboCallback = callback;
    ObjectMapper mapper = new ObjectMapper();
    JsonNode subscribe = mapper.valueToTree(Map.of("sub", bboString, "id", "bbo"));
    sendIfOpen(subscribe.toString());
  }

  @Override
  protected void handleEvent(JsonNode rootNode) {
    try {
      if (rootNode.has("ch")) {
        if (tradeDetailString.equals(rootNode.get("ch").asText())) {
          if (rootNode.has("tick")) {
            TradeDetailData[] data = objectMapper.treeToValue(
                rootNode.get("tick").get("data"), TradeDetailData[].class);
            this.tradeDetailCallback.onResponse(data[0]);
          }
        } else if (bboString.equals(rootNode.get("ch").asText())) {
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