package gt.trading.openbook;

import java.io.IOException;
import java.util.logging.FileHandler;

public final class FileHandlerSingleton {
  private static volatile FileHandler fileHandler = null;

  private FileHandlerSingleton() {
    return;
  }

  /**
   * Returns a copy of a FileHandler singleton. Checks if an instance is
   * already created. If not, it performs double-checked locking by
   * synchronizing on the class and checking again before creating a new
   * instance.
   *
   * @return FileHandler
   */
  public static FileHandler getInstance() {
    if (fileHandler == null) {
      synchronized (FileHandlerSingleton.class) {
        if (fileHandler == null) {
          try {
            fileHandler = new FileHandler("app/src/resources/logs/example.log");
          } catch (SecurityException | IOException e) {
            e.printStackTrace();
          }
        }
      }
    }

    return fileHandler;
  }
}
