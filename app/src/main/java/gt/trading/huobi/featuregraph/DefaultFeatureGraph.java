package gt.trading.huobi.featuregraph;

import java.util.function.Function;
import java.util.List;
import java.util.ArrayList;
import java.util.AbstractMap;
import java.util.Map;
import java.util.HashMap;

import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

import java.time.LocalDateTime;

import gt.trading.huobi.buckets.DepthData;
import gt.trading.huobi.buckets.TradeData;
import gt.trading.huobi.buckets.OrderBookData;

import gt.trading.huobi.featuregraph.Feature;

  /**
   * 
   * ! Can alienate each value from events and create their own lists. Ex: bestAskSize... from 
   * ! depthEvent
   */
public class DefaultFeatureGraph implements FeatureGraph {
  private List<Function<DepthData, Boolean>> depthEventCallbacks = new ArrayList<>();
  private List<Function<TradeData, Boolean>> tradeEventCallbacks = new ArrayList<>();
  private List<Function<OrderBookData, Boolean>> orderBookEventCallbacks = new ArrayList<>();

  private List<Feature> notProcessedFeatures = new ArrayList<>();
  private List<Feature> processedFeatures = new ArrayList<>();

  private Map<String, FeatureNode> featureNodes = new HashMap<>();

  private List<FeatureNode> depthAffectedNodes = new ArrayList<>();
  private List<FeatureNode> tradeAffectedNodes = new ArrayList<>();
  private List<FeatureNode> orderBookAffectedNodes = new ArrayList<>();

  private StringBuilder CSVBuilder = new StringBuilder();
  private final int CSV_MAX_ROWS = 100; // ! probably pass in with constructor
  private int CSVRowCount = 0;
  private final String CSFFolderName = "csvData"; // ! probably pass in with constructor

  private class FeatureNode {
    private List<Function<Feature, Boolean>> childrenOnUpdates = new ArrayList<>();
    private Feature feature;

    private boolean depthAffected = false;
    private boolean tradeAffected = false;
    private boolean orderBookAffected = false;

    public FeatureNode(Feature feature) {
      this.feature = feature;
    }

    public void addChildren(Feature feature,
        Function<Feature, Boolean> onParentUpdate) {
      childrenOnUpdates.add(onParentUpdate);
    }

    public void update() {
      this.feature.update();
      for (Function<Feature, Boolean> childOnUpdate : childrenOnUpdates) {
        childOnUpdate.apply(feature); // * ignoring return value
      }
    }

    public boolean getDepthAffected() {
      return this.depthAffected;
    }

    public boolean getTradeAffected() {
      return this.tradeAffected;
    }

    public boolean getOrderBookAffected() {
      return this.orderBookAffected;
    }

    public void addToDepthAffectedNodes() {
      if (!this.depthAffected) {
        depthAffectedNodes.add(this);
        this.depthAffected = true;
      }
    }

    public void addToTradeAffectedNodes() {
      if (!this.tradeAffected) {
        tradeAffectedNodes.add(this);
        this.tradeAffected = true;
      }
    }

    public void addToOrderBookAffectedNodes() {
      if (!this.orderBookAffected) {
        orderBookAffectedNodes.add(this);
        this.orderBookAffected = true;
      }
    }

  }

  public void addParent(Feature feature, Feature parentFeature,
      Function<Feature, Boolean> onParentUpdate) {
    FeatureNode node = this.featureNodes.get(feature.toString());
    FeatureNode parentNode = this.featureNodes.get(parentFeature.toString());

    if (parentNode.getDepthAffected()) {
      node.addToDepthAffectedNodes();
    }

    if (parentNode.getTradeAffected()) {
      node.addToTradeAffectedNodes();
    }

    if (parentNode.getOrderBookAffected()) {
      node.addToOrderBookAffectedNodes();
    }

    parentNode.addChildren(feature, onParentUpdate);
  }

  public void registerFeature(Feature feature, boolean shouldProcess) {
    this.featureNodes.put(feature.toString(), new FeatureNode(feature));
    if (shouldProcess) {
      processedFeatures.add(feature);
    } else {
      notProcessedFeatures.add(feature);
    }
  }

  public void registerDepthEventCallback(Feature feature,
      Function<DepthData, Boolean> onDepthEvent) {

    FeatureNode featureNode = featureNodes.get(feature.toString());
    featureNode.addToDepthAffectedNodes();

    depthEventCallbacks.add(onDepthEvent);
  }

  public void registerTradeEventCallback(Feature feature,
      Function<TradeData, Boolean> onTradeEvent) {

    FeatureNode featureNode = featureNodes.get(feature.toString());
    featureNode.addToTradeAffectedNodes();
    
    tradeEventCallbacks.add(onTradeEvent);
  }

  public void registerOrderBookEventCallback(Feature feature,
      Function<OrderBookData, Boolean> onOrderBookEvent) {

    FeatureNode featureNode = featureNodes.get(feature.toString());
    featureNode.addToOrderBookAffectedNodes();

    orderBookEventCallbacks.add(onOrderBookEvent);
  }

  public boolean onDepthEvent(DepthData depthData) {
    for (Function<DepthData, Boolean> callback : depthEventCallbacks) {
      callback.apply(depthData); // * ignoring return value
    }

    for (FeatureNode node: depthAffectedNodes) {
      node.update();
    }

    this.appendCSV();

    return true; // ! temporary
  }

  public boolean onTradeEvent(TradeData tradeData) {
    for (Function<TradeData, Boolean> callback : tradeEventCallbacks) {
      callback.apply(tradeData); // * ignoring return value
    }

    for (FeatureNode node: tradeAffectedNodes) {
      node.update();
    }

    this.appendCSV();

    return true; // ! temporary
  }

  public boolean onOrderBookEvent(OrderBookData orderBookData) {
    for (Function<OrderBookData, Boolean> callback : orderBookEventCallbacks) {
      callback.apply(orderBookData); // * ignoring return value
    }

    for (FeatureNode node: orderBookAffectedNodes) {
      node.update();
    }

    this.appendCSV();

    return true; // ! temporary
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (Feature feature : notProcessedFeatures) {
      builder.append(feature.toString() + ": " + feature.getValue() + "\n");
    }
    for (Feature feature : processedFeatures) {
      builder.append(feature.toString() + ": " + feature.getValue() + "\n");
    }
    return builder.toString();
  }

  private String getProcessedFeatureNames() {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < processedFeatures.size(); i++) {
      builder.append(processedFeatures.get(i).toString());
      if (i != processedFeatures.size() - 1) {
        builder.append(", ");
      }
    }
    return builder.toString();
  }

  private String toCSVRow() {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < processedFeatures.size(); i++) {
      builder.append(processedFeatures.get(i).getValue());
      if (i != processedFeatures.size() - 1) {
        builder.append(", ");
      }
    }
    return builder.toString();
  }

  private void appendCSV() {
    assert this.CSVRowCount < this.CSV_MAX_ROWS;

    if (this.CSVRowCount == 0) {
      this.CSVBuilder.append(this.getProcessedFeatureNames());
      this.CSVBuilder.append("\n");
    }

    this.CSVBuilder.append(this.toCSVRow());
    this.CSVBuilder.append("\n");
    this.CSVRowCount += 1;

    if (this.CSVRowCount == this.CSV_MAX_ROWS) {

      LocalDateTime now = LocalDateTime.now();
      String CSVFileName = now + ".csv";

      String savePath = this.CSFFolderName + "/" + CSVFileName;

      try (BufferedWriter writer = new BufferedWriter(new FileWriter(savePath))) {
        writer.write(this.CSVBuilder.toString());
        System.out.println("CSV file: " + CSVFileName + " saved.");
        CSVBuilder.setLength(0);
        this.CSVRowCount = 0;
      } catch (IOException e) {
          System.err.println("Error writing CSV file: " + e.getMessage());
      }
    }
  }

}
