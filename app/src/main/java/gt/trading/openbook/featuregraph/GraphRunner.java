package gt.trading.openbook.featuregraph;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import gt.trading.openbook.MapperSingleton;
import gt.trading.openbook.featuregraph.config.Config;
import gt.trading.openbook.listeners.MarketListener;

/**
 * Runs a feature graph and adds the features to a CSV file which is then added
 * to a specified folder.
 */
public final class GraphRunner {
  /**
   * Runs a feature graph and adds the features to a CSV file/folder whose path
   * is specified in the constructor.
   *
   * @param fileName the file to write CSV data to
   * @throws IOException an exception thrown if the data cannot be written
   */
  public GraphRunner(final String fileName) throws IOException {
    ObjectMapper mapper = MapperSingleton.getInstance();
    File jsonFile = new File(fileName);
    Config config = mapper.readValue(jsonFile, Config.class);
    String path = config.getBuilderPath();
    final Logger logger = Logger.getLogger(GraphRunner.class.getName());

    try {
      Class<?> customBuilderClass = Class.forName(path);
      logger.info("Class loaded: " + customBuilderClass.getName());

      DefaultGraph graph = new DefaultGraph();
      Object builderObject = customBuilderClass.getDeclaredConstructor()
          .newInstance();
      if (builderObject instanceof GraphBuilder) {
        GraphBuilder builder = (GraphBuilder) builderObject;
        builder.build(graph);

        MarketListener listener = new MarketListener();
        listener.connect("wss://api.huobi.pro/ws");
        listener.subscribeDepth(data -> {
          graph.onDepthEvent(data);
        });
      }
    } catch (ClassNotFoundException | NoSuchMethodException
        | IllegalAccessException | InvocationTargetException
        | InstantiationException error) {
      logger.severe("Error running graph: " + error.getMessage());
    }
  }
}
