package gt.trading.openbook;

import com.fasterxml.jackson.databind.ObjectMapper;

public final class MapperSingleton {
  private static volatile ObjectMapper objectMapper = null;

  private MapperSingleton() {
    return;
  }

  /**
   * Returns a copy of a ObjectMapper singleton. Checks if an instance is
   * already created. If not, it performs double-checked locking by
   * synchronizing on the class and checking again before creating a new
   * instance.
   *
   * @return ObjectMapper an instance of the Jackson ObjectMapper
   */
  public static ObjectMapper getInstance() {
    if (objectMapper == null) {
      synchronized (MapperSingleton.class) {
        if (objectMapper == null) {
          objectMapper = new ObjectMapper();
        }
      }
    }

    return objectMapper;
  }
}
