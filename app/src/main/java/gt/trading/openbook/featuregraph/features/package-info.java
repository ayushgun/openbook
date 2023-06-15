/**
 * This package contains basic features used within the feature graph.
 *
 * The BestAskFeature class is responsible for updating the best ask price
 * through callbacks whenever a DepthEvent occurs.
 *
 * The BestBidFeature class is responsible for updating the best bid price
 * through callbacks whenever a DepthEvent occurs.
 *
 * The MidPriceFeature class is responsible for updating the midprice and
 * is a child feature of the BestAskFeature and BestBidFeature. It is updated
 * through callbacks whenever the BestAsk or BestBid is updated.
 *
 * @author Georgia Tech Trading Club Team #2
 * @since 1.0
 */

package gt.trading.openbook.featuregraph.features;
