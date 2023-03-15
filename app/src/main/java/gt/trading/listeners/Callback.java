package gt.trading.listeners;

@FunctionalInterface
public interface Callback<T> {

  void onResponse(T response);
}
