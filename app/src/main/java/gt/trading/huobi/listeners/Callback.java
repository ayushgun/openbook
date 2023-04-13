package gt.trading.huobi.listeners;

/**
 * Functional interface for defining a callback function to be executed when a
 * response is received.
 *
 * @param <T> the type of the response object
 */
@FunctionalInterface
public interface Callback<T> {
  /**
   * Called when a response is received.
   *
   * @param response the response object of type T
   */
  void onResponse(T response);
}
