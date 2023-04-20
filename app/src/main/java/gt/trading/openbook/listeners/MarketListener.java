package gt.trading.openbook.listeners;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.WebSocketContainer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import gt.trading.openbook.models.DepthData;
import gt.trading.openbook.models.TradeData;

/**
 * The MarketListener class extends the Listener class to provide specific
 * implementations for handling market-related data events.
 */
@ClientEndpoint
public final class MarketListener extends Listener {
  private final String tradeDetailParams = "market.btcusdt.trade.detail";
  private final String depthParams = "market.btcusdt.bbo";
  private Callback<TradeData> tradeDetailCallback;
  private Callback<DepthData> depthCallback;
  private final ObjectMapper mapper = new ObjectMapper();
  private final Logger logger = Logger
      .getLogger(MarketListener.class.getName());

  /**
   * Creates a websocket connection to the market by price feed.
   *
   * @param uri the url to establish a websocket connection with
   */
  public void connect(final String uri) {
    WebSocketContainer container = ContainerProvider.getWebSocketContainer();

    try {
      container.connectToServer(this, URI.create(uri));
    } catch (DeploymentException | IOException error) {
      logger.severe(
          "Unable to establish a websocket connection: " + error.getMessage());
    }
  }

  /**
   * Subscribes to trade detail event and sets a callback to handle incoming
   * trade data.
   *
   * @param callback the callback to handle trade detail data
   */
  public void subscribeTradeDetail(final Callback<TradeData> callback) {
    tradeDetailCallback = callback;
    JsonNode subscribe = mapper.createObjectNode().put("sub", tradeDetailParams)
        .put("id", "trade_detail");
    send(subscribe);
  }

  /**
   * Subscribes to market depth event and sets a callback to handle incoming
   * depth data.
   *
   * @param callback the callback to handle depth data
   */
  public void subscribeDepth(final Callback<DepthData> callback) {
    depthCallback = callback;
    JsonNode subscribe = mapper.createObjectNode().put("sub", depthParams)
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

        if (tradeDetailParams.equals(channel)) {
          TradeData[] data = mapper.treeToValue(tickNode.get("data"),
              TradeData[].class);
          tradeDetailCallback.onResponse(data[0]);
        } else if (depthParams.equals(channel)) {
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
