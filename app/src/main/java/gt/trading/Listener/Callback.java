package gt.trading.Listener;

@FunctionalInterface
public interface Callback<T> {

  void onResponse(T response);
}
