package gt.trading.huobi.listeners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import gt.trading.huobi.buckets.DepthData;
import gt.trading.huobi.buckets.TradeData;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;


import okio.ByteString;

public interface RestListener {
  static final ObjectMapper mapper = new ObjectMapper();
  static HttpClient client = HttpClient.newHttpClient();
  static final String requestUrl = "https://api.huobi.pro/market/";
  static final List<String> messageList = new ArrayList<String>();

  default void getBBO() {
    getBBOhelper();
  }

  default void getTrade() {
    getTradeHelper();
  }


  private void getBBOhelper() {
    final String url = "https://api.huobi.pro/market/detail/merged?symbol=btcusdt";
    final HttpRequest request = HttpRequest.newBuilder()
      .uri(URI.create(url))
      .build();
    HttpResponse<String> response;
    try {
      response = client.send(request, HttpResponse.BodyHandlers.ofString());
      final JsonNode node = mapper.readTree(response.body());
      if (node.has("status") && node.get("status").asText().equals("ok")) {
        if (node.has("tick")) {
          DepthData depthData = null;
          System.out.println(depthData);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private void getTradeHelper() {
    final String url = "https://api.huobi.pro/market/trade?symbol=btcusdt";
    final HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url))
        .build();
    HttpResponse<String> response;
    try {
      response = client.send(request, HttpResponse.BodyHandlers.ofString());
      final JsonNode node = mapper.readTree(response.body());
      if (node.has("status") && node.get("status").asText().equals("ok")) {
        if (node.has("tick")) {
          TradeData tradeData = null;
          System.out.println(tradeData);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }



}
