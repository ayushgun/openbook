package gt.trading.huobi.listeners;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import gt.trading.huobi.buckets.DepthData;
import gt.trading.huobi.buckets.TradeData;

public class MarketListener extends Listener {
  private final String tradeDetailString = "market.btcusdt.trade.detail";
  private final String depthString = "market.btcusdt.bbo";
  private Callback<TradeData> tradeDetailCallback;
  private Callback<DepthData> depthCallback;

  /**
   * Subscribes to trade details.
   * 
   * @param callback callback function
   */
  public void subscribeTradeDetail(final Callback<TradeData> callback) {
    // Subscribe to BTC-USDT depth channel
    this.tradeDetailCallback = callback;
    ObjectMapper MAPPER = new ObjectMapper();
    JsonNode subscribe = MAPPER
        .valueToTree(Map.of("sub", tradeDetailString, "id", "trade_detail"));
    sendIfOpen(subscribe.toString());
  }

  /**
   * Subscribes to Best Bid/Offer.
   * 
   * @param callback callback function
   */
  public void subscribeDepth(final Callback<DepthData> callback) {
    // Subscribe to BTC-USDT depth channel
    this.depthCallback = callback;
    ObjectMapper MAPPER = new ObjectMapper();
    JsonNode subscribe = MAPPER
        .valueToTree(Map.of("sub", depthString, "id", "bbo"));
    sendIfOpen(subscribe.toString());
  }

  @Override
  protected void handleEvent(final JsonNode rootNode) {
    try {
      if (rootNode.has("ch")) {
        if (tradeDetailString.equals(rootNode.get("ch").asText())) {
          if (rootNode.has("tick")) {
            TradeData[] data = MAPPER.treeToValue(
                rootNode.get("tick").get("data"), TradeData[].class);
            this.tradeDetailCallback.onResponse(data[0]);
          }
        } else if (depthString.equals(rootNode.get("ch").asText())) {
          if (rootNode.has("tick")) {
            DepthData data = MAPPER.treeToValue(rootNode.get("tick"),
                DepthData.class);
            this.depthCallback.onResponse(data);
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
