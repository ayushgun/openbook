package gt.trading.huobi.featuregraph;

import java.io.IOException;
import java.io.File;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import gt.trading.huobi.featuregraph.config.FeatureGraphConfig;
import gt.trading.huobi.featuregraph.DefaultFeatureGraph;

import gt.trading.huobi.listeners.MarketListener;

import java.util.*;

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

        MarketListener listener = new MarketListener();
        listener.createWebSocketConnection("wss://api.huobi.pro/ws");
        listener.subscribeDepth(data -> {

          graph.onDepthEvent(data);

        });

        TimerTask task = new TimerTask() {
          @Override
          public void run() {
            System.out.println("Task executed at " + new Date());
            System.out.println(graph);

            System.out.println("");
          }
        };

        Timer timer = new Timer();
        timer.schedule(task, 0, 1000);
      }

    } catch (ClassNotFoundException e) {
      System.err.println("Class not found: " + path);
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
