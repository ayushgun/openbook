/**
 * This package contains event handlers for dealing with depth and trade data
 * from the Huobi cryptocurrency exchange.
 *
 * The DepthEvent class is responsible for handling depth events and updating
 * the best bid and ask prices.
 *
 * The TradeEvent class is responsible for handling trade detail events and
 * maintaining a list of up to 1000 most recent trade data entries.
 *
 * The OrderBook class is responsible for maintaining an order book given
 * incremental order book updates from Huobi. It stores bids and asks as
 * TreeMaps for efficient movement.
 *
 * @author Georiga Tech Trading Club Team #2
 * @since 1.0
 */
package gt.trading.huobi.events;
