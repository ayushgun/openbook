package gt.trading;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;

import okio.ByteString;

public class Utils {
  /**
   * Decodes byte string to byte array through decompression.
   * 
   * @param data byte string from websocket
   * @return byte array representation of byte string
   * @throws IOException if there is an error decompressing the streams
   */
  public static byte[] decode(ByteString data) throws IOException {
    ByteArrayInputStream bais = new ByteArrayInputStream(data.toByteArray());
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    decompress(bais, baos);
    baos.flush();
    baos.close();
    bais.close();

    return baos.toByteArray();
  }

  /**
   * 
   * Takes an input stream is and an output stream os and decompresses the gzip
   * compressed.
   * 
   * @param is input stream containing compressed data
   * @param os output stream to write decompressed data to
   * @throws IOException if there is an error reading or writing to the streams
   */
  private static void decompress(InputStream is, OutputStream os)
      throws IOException {
    GZIPInputStream gis = new GZIPInputStream(is);
    int count;
    byte[] data = new byte[1024];

    while ((count = gis.read(data, 0, 1024)) != -1) {
      os.write(data, 0, count);
    }

    gis.close();
  }
}
