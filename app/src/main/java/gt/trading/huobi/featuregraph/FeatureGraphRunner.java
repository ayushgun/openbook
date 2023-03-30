package gt.trading.huobi.featuregraph;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import gt.trading.huobi.featuregraph.config.FeatureGraphConfig;

public class FeatureGraphRunner {
  public static void run(String filename) throws IOException {
    ObjectMapper MAPPER = new ObjectMapper();
    File jsonFile = new File(filename);
    FeatureGraphConfig config = MAPPER.readValue(jsonFile,
        FeatureGraphConfig.class);
    String path = config.getBuilderPath();

    try {
      Class<?> cls = Class.forName(path);
      System.out.println("Class loaded: " + cls.getName());
    } catch (ClassNotFoundException e) {
      System.err.println("Class not found: " + path);
      e.printStackTrace();
    }
  }
}
