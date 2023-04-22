package gt.trading.huobi.core;

import java.io.BufferedWriter;

public final class LocalStorage<T> {
  private static final int DEFAULT_MAX_ROWS = 100;

  private final int csvMaxRows;
  private final String saveFolder;
  private final String fileName;

  private BufferedWriter bw;
  private int csvRowCount = 0;

  public static class Builder {
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
     */
    public fileName(final String val) {
      this.fileName = val;
    }

    /**
     * Set csvMaxRows.
     *
     * @param val
     */
    public csvMaxRows(final int val) {
      this.csvMaxRows = val;
    }

    /**
     * Build LocalStorage instance.
     *
     * @return instance
     */
    public LocalStorage build() {
      return new LocalStorage(this);
    }
  }

  /**
   * Hello.
   *
   * @param builder
   */
  private LocalStorage(final Builder builder) {

    this.csvMaxRows = builder.csvMaxRows;
    this.saveFolder = builder.saveFolder;

    if (builder.fileName.equals("")) {
      this.fileName = getNewFileName();
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
      csvRowCount += 1;
      if (csvRowCount >= csvMaxRows) {
        flushToFile();
      }
    } catch (Exception e) {
      System.out.println(e);
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

  private void flushToFile() {
    bw.flush();
    csvRowCount = 0;
  }

}
