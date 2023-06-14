package gt.trading.openbook.featuregraph;

import java.util.function.Function;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

import java.time.LocalDateTime;

import gt.trading.openbook.models.DepthData;
import gt.trading.openbook.models.TradeData;
import gt.trading.openbook.models.OrderBookData;

/**
 * Basic implementation of the FeatureGraph interface and is the current feature
 * graph implementation used in the application.
 */
public final class DefaultGraph implements FeatureGraph {
  private List<Function<DepthData, Boolean>> depthEventCallbacks =
    new ArrayList<>();
  private List<Function<TradeData, Boolean>> tradeEventCallbacks =
    new ArrayList<>();
  private List<Function<OrderBookData, Boolean>> orderBookEventCallbacks =
    new ArrayList<>();

  private List<Feature> notProcessedFeatures = new ArrayList<>();
  private List<Feature> processedFeatures = new ArrayList<>();

  private Map<String, FeatureNode> featureNodes = new HashMap<>();

  private List<FeatureNode> depthAffectedNodes = new ArrayList<>();
  private List<FeatureNode> tradeAffectedNodes = new ArrayList<>();
  private List<FeatureNode> orderBookAffectedNodes = new ArrayList<>();

  private StringBuilder csvBuilder = new StringBuilder();
  private final int csvMaxRows = 100;
  private int csvRowCount = 0;
  private final String csfFolderName = "app/csvData";

  private class FeatureNode {
    private List<Function<Feature, Boolean>> childrenOnUpdates =
      new ArrayList<>();
    private Feature feature;

    private boolean depthAffected = false;
    private boolean tradeAffected = false;
    private boolean orderBookAffected = false;

    /**
     * Sets the FeatureNode's feature to the feature passed in as a parameter.
     *
     * @param feat the feature to set the FeatureNode's feature to
     */
    FeatureNode(final Feature feat) {
      this.feature = feat;
    }

    /**
     * Adds children FeatureNode's to a FeatureNode.
     *
     * @param feat           the feature to add as a child
     * @param onParentUpdate callback function for when the parent node updates
     */
    public void addChildren(final Feature feat,
        final Function<Feature, Boolean> onParentUpdate) {
      childrenOnUpdates.add(onParentUpdate);
    }

    /**
     * Updates the feature node and all of its children.
     */
    public void update() {
      this.feature.update();
      for (Function<Feature, Boolean> childOnUpdate : childrenOnUpdates) {
        childOnUpdate.apply(feature);
      }
    }

    /**
     * FeatureNode's depthAffected getter method.
     *
     * @return the node's depthAffected value
     */
    public boolean getDepthAffected() {
      return this.depthAffected;
    }

    /**
     * FeatureNode's tradeAffected getter method.
     *
     * @return the node's tradeAffected value
     */
    public boolean getTradeAffected() {
      return this.tradeAffected;
    }

    /**
     * FeatureNode's orderBookAffected getter method.
     *
     * @return the node's orderBookAffected value
     */
    public boolean getOrderBookAffected() {
      return this.orderBookAffected;
    }

    /**
     * Adds the FeatureNode to a list of depthAffected nodes and changes the
     * depthAffected value to true.
     */
    public void addToDepthAffectedNodes() {
      if (!this.depthAffected) {
        depthAffectedNodes.add(this);
        this.depthAffected = true;
      }
    }

    /**
     * Adds the FeatureNode to a list of tradeAffected nodes and changes the
     * tradeAffected value to true.
     */
    public void addToTradeAffectedNodes() {
      if (!this.tradeAffected) {
        tradeAffectedNodes.add(this);
        this.tradeAffected = true;
      }
    }

    /**
     * Adds the FeatureNode to a list of orderBookAffected nodes and changes the
     * orderBookAffected value to true.
     */
    public void addToOrderBookAffectedNodes() {
      if (!this.orderBookAffected) {
        orderBookAffectedNodes.add(this);
        this.orderBookAffected = true;
      }
    }
  }

  /**
   * Add a parent feature node to a given feature node depending on what type of
   * feature (trade, depth, orderBookData) node it is.
   *
   * @param feature        the feature to add a parent to
   * @param parentFeature  the feature that will become the parent
   * @param onParentUpdate callback that occurs when the child is added
   */
  public void addParent(final Feature feature, final Feature parentFeature,
      final Function<Feature, Boolean> onParentUpdate) {
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

  /**
   * Constructs a new feature node and adds it to a list of processed or
   * unprocessed nodes.
   *
   * @param feature       the feature to be registered into the FeatureGraph
   * @param shouldProcess determines which list of features to add to
   */
  public void registerFeature(final Feature feature,
      final boolean shouldProcess) {
    this.featureNodes.put(feature.toString(), new FeatureNode(feature));
    if (shouldProcess) {
      processedFeatures.add(feature);
    } else {
      notProcessedFeatures.add(feature);
    }
  }

  /**
   * Method for feature to call its constructor in order for the feature graph
   * to register the depth callback function as an input for the feature.
   *
   * @param feature      the feature used to call a constructor
   * @param onDepthEvent callback that occurs on DepthEvents
   */
  public void registerDepthEventCallback(final Feature feature,
      final Function<DepthData, Boolean> onDepthEvent) {

    FeatureNode featureNode = featureNodes.get(feature.toString());
    featureNode.addToDepthAffectedNodes();

    depthEventCallbacks.add(onDepthEvent);
  }

  /**
   * Method for feature to call its constructor in order for the feature graph
   * to register the trade callback function as an input for the feature.
   *
   * @param feature      the feature used to call a constructor
   * @param onTradeEvent callback that occurs on TradeEvents
   */
  public void registerTradeEventCallback(final Feature feature,
      final Function<TradeData, Boolean> onTradeEvent) {

    FeatureNode featureNode = featureNodes.get(feature.toString());
    featureNode.addToTradeAffectedNodes();

    tradeEventCallbacks.add(onTradeEvent);
  }

  /**
   * Method for feature to call its constructor in order for the feature graph
   * to register the orderBook callback function as an input for the feature.
   *
   * @param feature          the feature used to call a constructor
   * @param onOrderBookEvent callback that occurs on OrderBookEvents
   */
  public void registerOrderBookEventCallback(final Feature feature,
      final Function<OrderBookData, Boolean> onOrderBookEvent) {

    FeatureNode featureNode = featureNodes.get(feature.toString());
    featureNode.addToOrderBookAffectedNodes();

    orderBookEventCallbacks.add(onOrderBookEvent);
  }

  /**
   * Updates the depthData and the corresponding node in the feature graph when
   * the listener receives new depthData.
   *
   * @param depthData the depthData the listener just received
   * @return true if successful
   */
  public boolean onDepthEvent(final DepthData depthData) {
    for (Function<DepthData, Boolean> callback : depthEventCallbacks) {
      callback.apply(depthData);
    }

    for (FeatureNode node : depthAffectedNodes) {
      node.update();
    }

    this.appendCSV();
    return true;
  }

  /**
   * Updates the tradeData and the corresponding node in the feature graph when
   * the listener receives new tradeData.
   *
   * @param tradeData the tradeData the listener just received
   * @return true if successful
   */
  public boolean onTradeEvent(final TradeData tradeData) {
    for (Function<TradeData, Boolean> callback : tradeEventCallbacks) {
      callback.apply(tradeData);
    }

    for (FeatureNode node : tradeAffectedNodes) {
      node.update();
    }

    this.appendCSV();
    return true;
  }

  /**
   * Updates the orderBookData and the corresponding node in the feature graph
   * when the listener receives new orderBookData.
   *
   * @param orderBookData the orderBookData the listener just received
   * @return true if successful
   */
  public boolean onOrderBookEvent(final OrderBookData orderBookData) {
    for (Function<OrderBookData, Boolean> callback : orderBookEventCallbacks) {
      callback.apply(orderBookData);
    }

    for (FeatureNode node : orderBookAffectedNodes) {
      node.update();
    }

    this.appendCSV();
    return true;
  }

  /**
   * Appends the non-processed and processed features to the csv file through a
   * string builder. Then it returns the builder as a string.
   *
   * @return a string of non-processed features proceeded by processed features
   */
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (Feature feature : notProcessedFeatures) {
      builder.append(feature.toString() + ":" + feature.getValue() + "\n");
    }
    for (Feature feature : processedFeatures) {
      builder.append(feature.toString() + ":" + feature.getValue() + "\n");
    }
    return builder.toString();
  }

  /**
   * Uses a string builder to append the names of all processed features.
   *
   * @return a string of processed feature names separated by commas
   */
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

  /**
   * Uses a string builder to append the names of all processed features to be
   * added as a string to a csv row.
   *
   * @return a string of processed information in CSV format
   */
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

  /**
   * Appends the featueres to a CSV file. If the CSV file reaches the maximum
   * number of rows, the file is saved and the features are appended to a new
   * CSV file.
   */
  private void appendCSV() {
    assert this.csvRowCount < csvMaxRows;

    if (this.csvRowCount == 0) {
      this.csvBuilder.append(this.getProcessedFeatureNames());
      this.csvBuilder.append("\n");
    }

    this.csvBuilder.append(this.toCSVRow());
    this.csvBuilder.append("\n");
    this.csvRowCount++;

    if (this.csvRowCount == this.csvMaxRows) {
      LocalDateTime now = LocalDateTime.now();
      String csvFileName = now + ".csv";
      csvFileName = csvFileName.replaceAll("[^a-zA-Z0-9.-]", "_");
      String savePath = this.csfFolderName + "/" + csvFileName;

      try (BufferedWriter writer = new BufferedWriter(
          new FileWriter(savePath))) {
        writer.write(this.csvBuilder.toString());
        System.out.println("CSV file: " + csvFileName + " saved.");
        csvBuilder.setLength(0);
        this.csvRowCount = 0;
      } catch (IOException e) {
        System.err.println("Error writing CSV file: " + e.getMessage());
      }
    }
  }
}
