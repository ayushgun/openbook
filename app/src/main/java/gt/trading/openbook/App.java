package gt.trading.openbook;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

// import gt.trading.openbook.core.Storage;
import gt.trading.openbook.listeners.MarketListener;

import gt.trading.openbook.featuregraph.GraphRunner;

/**
 * The main class for the order book application.
 *
 * This class sets up the application and starts the user interface. It creates
 * an instance of the order book and registers it with the user interface.
 */
public final class App {
  private static final Logger LOGGER = Logger.getLogger(App.class.getName());
  private static MarketListener marketListener = new MarketListener();

  private App() {
    return;
  }

  /**
   * The main method for running the order book.
   *
   * This method initializes an order book and starts processing order data. The
   * order book maintains a list of buy and sell orders for the BTC/USDT
   * instrument and matches them according to their prices and timestamps. It
   * also keeps track of the current bid and ask prices for the instrument.
   *
   * @param args an array of command line arguments (not used)
   */
  public static void main(final String[] args) {

    // S3 Code
    // MarketListener marketListener = new MarketListener();
    // OrderBookListener orderBookListener = new OrderBookListener();
    // Storage storage = new Storage();

    // marketListener.connect("wss://api-aws.huobi.pro/ws");
    // orderBookListener.connect("wss://api-aws.huobi.pro/feed");

    // marketListener.subscribeDepth(data -> {
    // storage.onDepthEvent(data);
    // });

    // marketListener.subscribeTradeDetail(data -> {
    // storage.onTradeEvent(data);
    // });

    // orderBookListener.subscribeMbp(data -> {
    // storage.onOrderBookEvent(data);
    // });

    try {
      new GraphRunner("app/src/resources/featuregraph/config/example.json",
          marketListener);
      CountDownLatch latch = new CountDownLatch(1);
      latch.await();
    } catch (IOException | InterruptedException error) {
      LOGGER.severe("Error starting application: " + error.getMessage());
    }
  }
}
