package gt.trading.openbook.featuregraph;

import java.util.function.Function;
import gt.trading.openbook.models.DepthData;
import gt.trading.openbook.models.TradeData;
import gt.trading.openbook.models.OrderBookData;

/**
 * Interface for implementing feature graphs.
 * Includes methods to register features and add parents to features.
 * Also includes callback functions when an event occurs or is registered.
 */
public interface FeatureGraph {
  /**
   * Provides custom logic for each event registered
   * inside the feature graph.
   *
   * @param feature The feature to be registered into the graph.
   * @param shouldProcess Boolean to determine if the feature should be added.
   */
  void registerFeature(Feature feature, boolean shouldProcess);

  /**
   * Adds a parent to a feature already inside the feature graph.
   *
   * @param feature The feature to add a parent to.
   * @param parentFeature The feature that should be the parent
   * @param onParentUpdate
   */
  void addParent(Feature feature, Feature parentFeature,
    Function<Feature, Boolean> onParentUpdate);

  /**
   * Method which depthEvent features call to ensure that they
   * have been added to the graph.
   *
   * @param feature
   * @param onDepthEvent
   */
  void registerDepthEventCallback(Feature feature,
    Function<DepthData, Boolean> onDepthEvent);

  /**
   * Method which tradeEvent features call to ensure that they
   * have been added to the graph.
   *
   * @param feature
   * @param onTradeEvent
   */
  void registerTradeEventCallback(Feature feature,
    Function<TradeData, Boolean> onTradeEvent);

  /**
   * Method which orderBookEvent features call to ensure that they
   * have been added to the graph.
   *
   * @param feature
   * @param onOrderBookEvent
   */
  void registerOrderBookEventCallback(Feature feature,
    Function<OrderBookData, Boolean> onOrderBookEvent);

  /**
   * Provides custom logic for the feature graph when a listener
   * receives depthData.
   *
   * @param depthData
   * @return true
   */
  boolean onDepthEvent(DepthData depthData);

  /**
   * Provides custom logic for the feautre graph when a listener
   * receives tradeData.
   *
   * @param tradeData
   * @return true
   */
  boolean onTradeEvent(TradeData tradeData);

  /**
   * Provides custom logic for the feature graph when a listener
   * receives orderBookData.
   *
   * @param orderBookData
   * @return true
   */
  boolean onOrderBookEvent(OrderBookData orderBookData);

  @Override
  String toString();
}
