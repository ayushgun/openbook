package gt.trading.openbook;

import java.io.IOException;
import java.util.logging.Logger;

import gt.trading.openbook.featuregraph.GraphRunner;
import gt.trading.openbook.listeners.MarketListener;

public class FeatureGraphThread implements Runnable {
  private static final Logger LOGGER = Logger.getLogger(App.class.getName());
  private MarketListener marketListener;

  /**
   *
   * @param listener
   */
  public FeatureGraphThread(final MarketListener listener) {
    this.marketListener = listener;
  }

  /**
   *
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
