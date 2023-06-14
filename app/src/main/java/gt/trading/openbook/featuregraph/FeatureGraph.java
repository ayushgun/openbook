package gt.trading.openbook.featuregraph;

import java.util.function.Function;
import gt.trading.openbook.models.DepthData;
import gt.trading.openbook.models.TradeData;
import gt.trading.openbook.models.OrderBookData;

/**
 * Interface for implementing feature graphs. Includes methods to register
 * features and add parents to features. Also includes callback functions when
 * an event occurs or is registered.
 */
public interface FeatureGraph {
  /**
   * Provides custom logic for each event registered inside the feature graph.
   *
   * @param feature       the feature to be registered into the FeatureGraph
   * @param shouldProcess determines which list of features to add to
   */
  void registerFeature(Feature feature, boolean shouldProcess);

  /**
   * Adds a parent to a feature already inside the feature graph.
   *
   * @param feature        the feature to add a parent to
   * @param parentFeature  the feature that should be the parent
   * @param onParentUpdate callback that occurs when the child is added
   */
  void addParent(Feature feature, Feature parentFeature,
      Function<Feature, Boolean> onParentUpdate);

  /**
   * Method which depthEvent features call to ensure that they have been added
   * to the graph.
   *
   * @param feature      the feature used to call a constructor
   * @param onDepthEvent callback that occurs on DepthEvents
   */
  void registerDepthEventCallback(Feature feature,
      Function<DepthData, Boolean> onDepthEvent);

  /**
   * Method which tradeEvent features call to ensure that they have been added
   * to the graph.
   *
   * @param feature      the feature used to call a constructor
   * @param onTradeEvent callback that occurs on TradeEvents
   */
  void registerTradeEventCallback(Feature feature,
      Function<TradeData, Boolean> onTradeEvent);

  /**
   * Method which orderBookEvent features call to ensure that they have been
   * added to the graph.
   *
   * @param feature          the feature used to call a constructor
   * @param onOrderBookEvent callback that occurs on OrderBookEvents
   */
  void registerOrderBookEventCallback(Feature feature,
      Function<OrderBookData, Boolean> onOrderBookEvent);

  /**
   * Provides custom logic for the feature graph when a listener receives
   * depthData.
   *
   * @param depthData the depthData the listener just received
   * @return true if successful
   */
  boolean onDepthEvent(DepthData depthData);

  /**
   * Provides custom logic for the feautre graph when a listener receives
   * tradeData.
   *
   * @param tradeData the tradeData the listener just received
   * @return true if successful
   */
  boolean onTradeEvent(TradeData tradeData);

  /**
   * Provides custom logic for the feature graph when a listener receives
   * orderBookData.
   *
   * @param orderBookData the orderBookData the listener just received
   * @return true if successful
   */
  boolean onOrderBookEvent(OrderBookData orderBookData);

  @Override
  String toString();
}
