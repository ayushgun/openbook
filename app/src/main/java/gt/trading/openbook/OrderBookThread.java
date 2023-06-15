package gt.trading.openbook;

import java.util.concurrent.CountDownLatch;

import gt.trading.openbook.core.OrderBook;
import gt.trading.openbook.listeners.OrderBookListener;

public class OrderBookThread implements Runnable {
  private OrderBookListener orderBookListener;

  /**
   * Constructor for an OrderBookThread.
   *
   * @param sharedListener reference to an OrderBookListener to be used in
   *                       OrderBook.
   */
  public OrderBookThread(final OrderBookListener sharedListener) {
    orderBookListener = sharedListener;
  }

  /**
   * Runs an order book.
   */
  public void run() {
    new OrderBook(orderBookListener);
    CountDownLatch latch = new CountDownLatch(1);

    try {
      latch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
