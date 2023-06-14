package gt.trading.openbook;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import gt.trading.openbook.featuregraph.GraphRunner;

/**
 * The main class for the order book application.
 *
 * This class sets up the application and starts the user interface. It creates
 * an instance of the order book and registers it with the user interface.
 */
public final class App {
  private static final Logger LOGGER = Logger.getLogger(App.class.getName());

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
    try {
      Package currentPackage = App.class.getPackage();
      String packagePath = currentPackage.getName().replace(".", "/");
      String sourcePath = "app/src/main/java/" + packagePath + "/";
      new GraphRunner(sourcePath + "featuregraph/config/example.json");
      CountDownLatch latch = new CountDownLatch(1);
      latch.await();
    } catch (IOException | InterruptedException error) {
      LOGGER.severe("Error processing JSON data: " + error.getMessage());
    }
  }
}
