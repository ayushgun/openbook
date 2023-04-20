package gt.trading.huobi.core;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.core.sync.RequestBody;

import java.io.File;

import com.fasterxml.jackson.databind.ObjectMapper;

import gt.trading.huobi.listeners.MarketListener;
import gt.trading.huobi.models.DepthData;

public class Storage {

  /**
   * Hello.
   */
  public Storage() {
    MarketListener listener = new MarketListener();
    listener.connect("wss://api-aws.huobi.pro/ws");
    ObjectMapper objectMapper = new ObjectMapper();
    listener.subscribeDepth(data -> {
      try {
        String jsonString = objectMapper.writeValueAsString(data);
        System.out.println(jsonString);
      } catch (Exception e) {
        System.out.println(e);
      }
    });
  }

  /**
   * Heloo.
   *
   * @param data
   */
  public void onDepthEvent(final DepthData data) {
    // System.out.println(jsonString);
  }

  /**
   * Hello.
   */
  public void uploadData() {
    String bucketname = "huobi";
    String filename = "test-file";
    String filepath = "/Users/giovanni/Development/Quant/aws-s3-setup/cat.jpg";

    S3Client client = S3Client.builder()
        .credentialsProvider(null)
        .region(Region.US_EAST_1)
        .build();

    PutObjectRequest request = PutObjectRequest.builder()
            .bucket(bucketname)
            .key(filename)
            .build();

    client.putObject(request, RequestBody.fromFile(new File(filepath)));
  }
}
