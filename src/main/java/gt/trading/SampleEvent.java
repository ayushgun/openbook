package gt.trading;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import okhttp3.WebSocket;

/**
 * Object implementation of the Listener interface
 */
public class SampleEvent extends Listener {
  /**
   * Handles custom logic for each event type implemented.
   * 
   * @param json json data to parse and handle
   */
  public void handleEvent(JSONObject json) {
    if (json.containsKey("ch")) {
      JSONObject depth = json.getJSONObject("tick");
      JSONArray bids = (JSONArray) depth.get("bids");
      JSONArray asks = (JSONArray) depth.get("asks");

      // Print five latest bids to standard output
      System.out.println("Bids:");
      for (int i = 0; i < Math.min(bids.size(), 5); i++) {
        JSONArray order = (JSONArray) bids.get(i);
        Number price = (Number) order.get(0);
        Number quantity = (Number) order.get(1);
        System.out.println(price + ": " + quantity);
      }

      // Print five latest asks to standard output
      System.out.println("Asks:");
      for (int i = 0; i < Math.min(asks.size(), 5); i++) {
        JSONArray order = (JSONArray) asks.get(i);
        Number price = (Number) order.get(0);
        Number quantity = (Number) order.get(1);
        System.out.println(price + ": " + quantity);
      }
    }
  }

  @Override
  public void subscribe(WebSocket webSocket) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'subscribe'");
  }

  @Override
  public void handleEvent(String json) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException(
        "Unimplemented method 'handleEvent'");
  }
}
