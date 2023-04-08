package gt.trading.huobi.listeners;

import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import gt.trading.huobi.models.DepthData;
import gt.trading.huobi.models.TradeData;

/**
 * The MarketListener class extends the Listener class to provide specific
 * implementations for handling market-related data events.
 */
public class MarketListener extends Listener {
  private final String tradeSymbol = "market.btcusdt.trade.detail";
  private final String depthSymbol = "market.btcusdt.bbo";
  private Callback<TradeData> tradeDetailCallback;
  private Callback<DepthData> depthCallback;
  private final ObjectMapper mapper = new ObjectMapper();
  private final Logger logger = Logger.getLogger(Listener.class.getName());

  /**
   * Subscribes to trade detail events and sets a callback to handle incoming
   * trade detail data.
   *
   * @param callback the callback to handle trade detail data
   */
  public void subscribeTradeDetail(final Callback<TradeData> callback) {
    tradeDetailCallback = callback;
    JsonNode subscribe = mapper.createObjectNode().put("sub", tradeSymbol)
        .put("id", "trade_detail");
    send(subscribe);
  }

  /**
   * Subscribes to market depth events and sets a callback to handle incoming
   * depth data.
   *
   * @param callback the callback to handle depth data
   */
  public void subscribeDepth(final Callback<DepthData> callback) {
    depthCallback = callback;
    JsonNode subscribe = mapper.createObjectNode().put("sub", depthSymbol)
        .put("id", "bbo");
    send(subscribe);
  }

  /**
   * Handles incoming event data by processing the received JsonNode object and
   * invoking the appropriate callback based on the event type.
   *
   * @param json the JsonNode containing the event data
   */
  @Override
  protected void handleEvent(final JsonNode json) {
    try {
      if (json.has("ch") && json.has("tick")) {
        String channel = json.get("ch").asText();
        JsonNode tickNode = json.get("tick");

        if (tradeSymbol.equals(channel)) {
          TradeData[] data = mapper.treeToValue(tickNode.get("data"),
              TradeData[].class);
          tradeDetailCallback.onResponse(data[0]);
        } else if (depthSymbol.equals(channel)) {
          DepthData data = mapper.treeToValue(tickNode, DepthData.class);
          depthCallback.onResponse(data);
        } else {
          logger.warning("JSON data does not fit in any channel: " + json);
        }
      } else if (json.has("status")) {
        logger.info("Status: " + json);
      } else {
        logger.warning("JSON data does not fit in any category: " + json);
      }
    } catch (JsonMappingException error) {
      logger.severe("Error in mapping JSON data: " + error.getMessage());
    } catch (JsonProcessingException error) {
      logger.severe("Error processing JSON data: " + error.getMessage());
    }
  }
}
