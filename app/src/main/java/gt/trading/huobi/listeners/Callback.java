package gt.trading.huobi.listeners;

@FunctionalInterface
public interface Callback<T> {
  void onResponse(final T response);
}
