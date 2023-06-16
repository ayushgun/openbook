package gt.trading.openbook;

import java.util.logging.SimpleFormatter;

public final class SimpleFormatterSingleton {
  private static volatile SimpleFormatter simpleFormatter = null;

  private SimpleFormatterSingleton() {
    return;
  }

  /**
   * Returns a copy of a SimpleFormatter singleton. Checks if an instance is
   * already created. If not, it performs double-checked locking by
   * synchronizing on the class and checking again before creating a new
   * instance.
   *
   * @return SimpleFormatter
   */
  public static SimpleFormatter getInstance() {
    if (simpleFormatter == null) {
      synchronized (SimpleFormatterSingleton.class) {
        if (simpleFormatter == null) {
          simpleFormatter = new SimpleFormatter();
        }
      }
    }

    return simpleFormatter;
  }
}
