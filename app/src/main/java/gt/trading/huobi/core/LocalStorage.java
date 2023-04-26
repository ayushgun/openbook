package gt.trading.huobi.core;

import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class LocalStorage<T> {
  private static final int DEFAULT_MAX_ROWS = 100;

  private final int csvMaxRows;
  private final String saveFolder;
  private final String fileName;

  private final ObjectMapper objectMapper = new ObjectMapper();
  private BufferedWriter bw;
  private int csvRowCount = 0;

  public static class Builder<T> {
    private final String saveFolder;

    private String fileName = "";
    private int csvMaxRows = DEFAULT_MAX_ROWS;

    /**
     * Builder Constructor.
     *
     * @param newSaveFolder
     */
    public Builder(final String newSaveFolder) {
      this.saveFolder = newSaveFolder;
    }

    /**
     * Set fileName.
     *
     * @param val
     * @return builder
     */
    public Builder<T> fileName(final String val) {
      this.fileName = val;
      return this;
    }

    /**
     * Set csvMaxRows.
     *
     * @param val
     * @return builder
     */
    public Builder<T> csvMaxRows(final int val) {
      this.csvMaxRows = val;
      return this;
    }

    /**
     * Build LocalStorage instance.
     *
     * @return instance
     */
    public LocalStorage<T> build() throws IOException {
      return new LocalStorage(this);
    }
  }

  /**
   * Hello.
   *
   * @param builder
   */
  private LocalStorage(final Builder<T> builder) throws IOException {

    this.csvMaxRows = builder.csvMaxRows;
    this.saveFolder = builder.saveFolder;

    if (builder.fileName.equals("")) {
      this.fileName = getTimeFileName();
    } else {
      this.fileName = builder.fileName;
    }

    this.bw = new BufferedWriter(new FileWriter(getFilePath(), true));
  }

  /**
   * Hello.
   *
   * @param data
   */
  public void onEvent(final T data) {
    try {
      bw.append(objectMapper.writeValueAsString(data));
      bw.newLine();
      csvRowCount += 1;
      if (csvRowCount >= csvMaxRows) {
        flushToFile();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Hello.
   *
   * @return fileName
   */
  public String getFilePath() {
    return this.saveFolder + "/" + this.fileName;
  }

  /**
   * ! prob should handle exceptions differently
   *
   * @throws IOException
   */
  private void flushToFile() throws IOException {
    bw.flush();
    System.out.println("Flushed " + this.saveFolder);
    csvRowCount = 0;
  }

  private String getTimeFileName() {
    LocalDateTime currentDateTime = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter
        .ofPattern("yyyy-MM-dd HH:mm:ss");
    String formattedDateTime = currentDateTime.format(formatter);
    return formattedDateTime + ".json";
  }

}
