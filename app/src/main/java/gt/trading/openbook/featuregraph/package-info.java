/**
 * This package contains classes for building and running feature graphs use
 * OrderBook data to dynamically represent relationships between research
 * metrics.
 *
 * The DefaultGraph class provides a feature graph that implements all necessary
 * methods for a graph to properly run. These methods mainly involve registering
 * and updating features, as well as writing the features' data to a CSV file.
 *
 * The ExampleGraphBuilder class provides an implementation of the GraphBuilder
 * interface's build method. Currently, it builds with only a BestAskFeature,
 * BestBidFeature, and MidPriceFeature.
 *
 * The Feature interface is responsible for providing a list of methods that all
 * features must implement. These methods include basic getter and toString
 * methods, as well as callback functions for when a listener receives data.
 *
 * The FeatureGraph interface is responsible for providing a list of methods
 * that any feature graph must implement, such as registering features and
 * updating them when data is received.
 *
 * @author Georgia Tech Trading Club Team #2
 * @since 1.0
 */

package gt.trading.openbook.featuregraph;
