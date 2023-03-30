package gt.trading.huobi.featuregraph;

import java.io.IOException;
import java.io.File;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import gt.trading.huobi.featuregraph.config.FeatureGraphConfig;
import gt.trading.huobi.featuregraph.DefaultFeatureGraph;

public class FeatureGraphRunner {
  public static void run(String filename) throws IOException {
    ObjectMapper MAPPER = new ObjectMapper();
    File jsonFile = new File(filename);
    FeatureGraphConfig config = MAPPER.readValue(jsonFile,
        FeatureGraphConfig.class);
    String path = config.getBuilderPath();

    try {
      Class<?> CustomBuilderClass = Class.forName(path);
      System.out.println("Class loaded: " + CustomBuilderClass.getName());

      DefaultFeatureGraph graph = new DefaultFeatureGraph();
      Object builderObj = CustomBuilderClass.getDeclaredConstructor()
          .newInstance();
      if (builderObj instanceof FeatureGraphBuilder) {
        FeatureGraphBuilder builder = (FeatureGraphBuilder) builderObj;
        builder.build(graph);
      }

    } catch (ClassNotFoundException e) {
      System.err.println("Class not found: " + path);
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
