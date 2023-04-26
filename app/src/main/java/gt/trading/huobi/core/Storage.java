package gt.trading.huobi.core;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.core.sync.RequestBody;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import com.fasterxml.jackson.databind.ObjectMapper;

import gt.trading.huobi.models.DepthData;
import gt.trading.huobi.models.TradeData;
import gt.trading.huobi.models.OrderBookData;

public class Storage {
  private ObjectMapper objectMapper = new ObjectMapper();
  private LocalStorage<DepthData> depthLocalStorage;
  private LocalStorage<TradeData> tradeLocalStorage;
  private LocalStorage<OrderBookData> orderBookLocalStorage;

  private static final int INTERVAL_MS = 5000;

  /**
   * Hello.
   */
  public Storage() {
    try {
      depthLocalStorage = new LocalStorage.
      Builder<DepthData>("storageData/depthData").build();

      tradeLocalStorage = new LocalStorage.
      Builder<TradeData>("storageData/tradeData").build();

      orderBookLocalStorage = new LocalStorage.
      Builder<OrderBookData>("storageData/orderBookData").build();

      Timer timer = new Timer();
      TimerTask task = new TimerTask() {
          public void run() {
              // Code to be executed repeatedly
              uploadData(depthLocalStorage.getFilePath());
              uploadData(tradeLocalStorage.getFilePath());
              uploadData(orderBookLocalStorage.getFilePath());
              System.out.println("S3 uploaded!");
          }
      };
      timer.scheduleAtFixedRate(task, 0, INTERVAL_MS);

    } catch (Exception e) {
      e.printStackTrace();
    }
    // MarketListener listener = new MarketListener();
    // listener.connect("wss://api-aws.huobi.pro/ws");
    // ObjectMapper objectMapper = new ObjectMapper();
    // listener.subscribeDepth(data -> {
    //   try {
    //     String jsonString = objectMapper.writeValueAsString(data);
    //     System.out.println(jsonString);
    //   } catch (Exception e) {
    //     System.out.println(e);
    //   }
    // });

  }

  /**
   * Heloo.
   *
   * @param data
   */
  public void onDepthEvent(final DepthData data) {
    try {
      String jsonString = objectMapper.writeValueAsString(data);
      // System.out.println(jsonString);
      // System.out.println("Depth");
      depthLocalStorage.onEvent(data);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Heloo.
   *
   * @param data
   */
  public void onTradeEvent(final TradeData data) {
    try {
      String jsonString = objectMapper.writeValueAsString(data);
      // System.out.println(jsonString);
      // System.out.println("Tradeee");

      tradeLocalStorage.onEvent(data);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Heloo.
   *
   * @param data
   */
  public void onOrderBookEvent(final OrderBookData data) {
    try {
      String jsonString = objectMapper.writeValueAsString(data);
      // System.out.println(jsonString);
      // System.out.println("OrderBook");

      orderBookLocalStorage.onEvent(data);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Hello.
   *
   * @param filename
   */
  public void uploadData(final String filename) {
    String bucketname = "huobi";
    // String filename = "test-file";
    String filepath = filename;

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
