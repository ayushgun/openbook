package gt.trading.huobi.listeners;

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

import gt.trading.huobi.models.OrderBookData;

/**
 * The OrderBookListener class extends the Listener class to provide specific
 * implementations for handling order book snapshat data events.
 */
@ClientEndpoint
public class OrderBookListener extends Listener {
  private final String mbpSymbol = "market.btcusdt.mbp.150";
  private Callback<OrderBookData> mbpCallback;
  private final ObjectMapper mapper = new ObjectMapper();
  private final Logger logger = Logger
      .getLogger(OrderBookListener.class.getName());

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
   * Subscribes to market by price event and sets a callback to handle incoming
   * order book data.
   *
   * @param callback the callback to handle trade detail data
   */
  public void subscribeMbp(final Callback<OrderBookData> callback) {
    mbpCallback = callback;
    JsonNode subscribe = mapper.createObjectNode().put("sub", mbpSymbol)
        .put("id", "id1");
    send(subscribe);
  }

  /**
   * Requests a full order book snapshot from the Huobi websocket. The server
   * will respond with a complete snapshot of the current order book state,
   * which will be processed by the handleEvent method in this listener.
   */
  public void refresh() {
    JsonNode request = mapper.createObjectNode().put("req", mbpSymbol).put("id",
        "id2");
    send(request);
  }

  /**
   * Handles incoming event data by processing the received JsonNode object and
   * invoking the order book callback.
   *
   * @param json the JsonNode containing the event data
   */
  @Override
  protected void handleEvent(final JsonNode json) {
    try {
      if (json.has("id") && "id2".equals(json.get("id").asText())) {
        OrderBookData data = mapper.treeToValue(json.get("data"),
            OrderBookData.class);
        data.setAction("REFRESH");
        mbpCallback.onResponse(data);
      } else if (json.has("ch") && mbpSymbol.equals(json.get("ch").asText())
          && json.has("tick")) {
        OrderBookData data = mapper.treeToValue(json.get("tick"),
            OrderBookData.class);
        data.setAction("INCREMENT");
        mbpCallback.onResponse(data);
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
