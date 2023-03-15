package gt.trading;

@FunctionalInterface
public interface Callback<T> {

  void onResponse(T response);
}
