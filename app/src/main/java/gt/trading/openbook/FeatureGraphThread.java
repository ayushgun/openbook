package gt.trading.openbook;

import java.io.IOException;
import java.util.logging.Logger;

import gt.trading.openbook.featuregraph.GraphRunner;

public class FeatureGraphThread implements Runnable {
  private static final Logger LOGGER = Logger.getLogger(App.class.getName());
  public FeatureGraphThread () {

  }

  public void run() {
    try {
      new GraphRunner("app/src/resources/featuregraph/config/example.json");
    } catch (IOException error) {
      LOGGER.severe("Error starting application: " + error.getMessage());
    }
  }
}
