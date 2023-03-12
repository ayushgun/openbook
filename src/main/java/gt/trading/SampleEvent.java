package gt.trading;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONArray;

/**
 * Sample event that implements the Listener interface.
 */
public class SampleEvent extends Listener{
  
  /**
   * Handles custom logic depending on event type.
   * 
   * @param json  json object containing data to parse
   */
  public void handleEvent(JSONObject json){
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
}
