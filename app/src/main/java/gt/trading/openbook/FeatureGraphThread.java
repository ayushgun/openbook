package gt.trading.openbook;

import java.io.IOException;
import java.util.logging.Logger;

import gt.trading.openbook.featuregraph.GraphRunner;
import gt.trading.openbook.listeners.MarketListener;

public class FeatureGraphThread implements Runnable {
  private static final Logger LOGGER = Logger.getLogger(App.class.getName());
  private MarketListener marketListener;

  /**
   * Constructor for a FeatureGraphThread.
   *
   * @param sharedListener reference to a MarketListener to be used in
   *                       GraphRunner
   */
  public FeatureGraphThread(final MarketListener sharedListener) {
    marketListener = sharedListener;
  }

  /**
   * Runs a feature graph.
   */
  public final void run() {
    try {
      new GraphRunner("app/src/resources/featuregraph/config/example.json",
          marketListener);
    } catch (IOException error) {
      LOGGER.severe("Error starting application: " + error.getMessage());
    }
  }
}
