package gt.trading.openbook.featuregraph;

import java.util.function.Function;
import gt.trading.openbook.models.DepthData;
import gt.trading.openbook.models.TradeData;
import gt.trading.openbook.models.OrderBookData;

public interface FeatureGraph {
  /**
   * 
   * @param feature
   * @param shouldProcess
   */
  void registerFeature(Feature feature, boolean shouldProcess);

  /**
   * 
   * @param feature
   * @param parentFeature
   * @param onParentUpdate
   */
  void addParent(Feature feature, Feature parentFeature,
    Function<Feature, Boolean> onParentUpdate);
  
  /**
   * 
   * @param feature
   * @param onDepthEvent
   */
  void registerDepthEventCallback(Feature feature,
    Function<DepthData, Boolean> onDepthEvent);
  
  /**
   * 
   * @param feature
   * @param onTradeEvent
   */
  void registerTradeEventCallback(Feature feature,
    Function<TradeData, Boolean> onTradeEvent);
  
  /**
   * 
   * @param feature
   * @param onOrderBookEvent
   */
  void registerOrderBookEventCallback(Feature feature,
    Function<OrderBookData, Boolean> onOrderBookEvent);
  
  /**
   * 
   * @param depthData
   * @return
   */
  boolean onDepthEvent(DepthData depthData);
  
  /**
   * 
   * @param tradeData
   * @return
   */
  boolean onTradeEvent(TradeData tradeData);

  /**
   * 
   * @param orderBookData
   * @return
   */
  boolean onOrderBookEvent(OrderBookData orderBookData);

  @Override
  String toString();
}
