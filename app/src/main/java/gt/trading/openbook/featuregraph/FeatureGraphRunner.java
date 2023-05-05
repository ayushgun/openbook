package gt.trading.openbook.featuregraph;

import java.io.IOException;
import java.io.File;
import com.fasterxml.jackson.databind.ObjectMapper;

import gt.trading.openbook.featuregraph.config.FeatureGraphConfig;
import gt.trading.openbook.listeners.MarketListener;

public final class FeatureGraphRunner {
  /**
   * Runs a feature graph and adds the features to a CSV file/folder
   * whose path is specified in the constructor.
   *
   * @param fileName
   * @throws IOException
   */
  public FeatureGraphRunner(final String fileName) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    File jsonFile = new File(fileName);
    FeatureGraphConfig config = mapper.readValue(jsonFile,
        FeatureGraphConfig.class);
    String path = config.getBuilderPath();

    try {
      Class<?> customBuilderClass = Class.forName(path);
      System.out.println("Class loaded: " + customBuilderClass.getName());

      DefaultFeatureGraph graph = new DefaultFeatureGraph();
      Object builderObject = customBuilderClass.getDeclaredConstructor()
          .newInstance();
      if (builderObject instanceof FeatureGraphBuilder) {
        FeatureGraphBuilder builder  = (FeatureGraphBuilder) builderObject;
        builder.build(graph);

        MarketListener listener = new MarketListener();
        listener.connect("wss://api.huobi.pro/ws");
        listener.subscribeDepth(data -> {
          graph.onDepthEvent(data);
        });
      }
    } catch (Exception e) {
      System.err.println(e.getMessage());
      e.printStackTrace();
    }
  }
}
