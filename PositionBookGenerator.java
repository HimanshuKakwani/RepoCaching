//package caching;
package caching;

import org.json.JSONObject;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import javax.websocket.OnClose;

public class PositionBookGenerator {

    // LRU Cache with LinkedHashMap
    static class LRUCache<K, V> extends LinkedHashMap<K, V> {
        private final int maxEntries;

        public LRUCache(int maxEntries) {
            super(maxEntries + 1, 1.0f, true);
            this.maxEntries = maxEntries;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > maxEntries;
        }
    }

    public static void main(String[] args) {
        // Input strings
        String[] orders = {
            "1|itc|nse|001|10|8.2|B", 
            "2|acc|bse|003|2|15.0|S", 
            "1|itc|bse|002|1|8.4|B", 
            "1|tcs|bse|004|12|16.0|B",
            "1|itc|nse|008|5|8.3|S"
        };
        
        final long startTime1 = System.currentTimeMillis();

        // Initialize LRU cache with a max size of 10
        LRUCache<String, JSONObject> cache = new LRUCache<>(10000);

        // Process orders to JSON format
        JSONObject usersJson = processOrdersToJson(orders);
        
        for(Object key:usersJson.keySet()) {
        	String keyStr = (String) key;
        	JSONObject value = (JSONObject) usersJson.get(keyStr);
        	cache.put(keyStr, value);
        }
        System.out.println("Cache size = " + cache.size());
        System.out.println("Cache is: " + cache);
        
//        // Store in LRU cache
//        cache.put("user_data", usersJson);
        
    	final long elapsedTime1 = System.currentTimeMillis()-startTime1;
    	System.out.println("JSON time -------> " + elapsedTime1 + " ms");

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter cust id: ");
        String customerId = scanner.nextLine();
        scanner.close();
        
        final long startTime = System.currentTimeMillis();
    	// Generate Position Book CSV for each user
        generatePositionBookCsv(cache, customerId); // Example for user ID 1
        final long elapsedTime = System.currentTimeMillis()-startTime;
    	System.out.println("processing time -------> " + elapsedTime + " ms");
    }

    private static JSONObject processOrdersToJson(String[] orders) {
        JSONObject usersJson = new JSONObject();
        for (String order : orders) {
            String[] parts = order.split("\\|");
            String userId = parts[0];
            String scrip = parts[1];
            String exchange = parts[2];
            String orderId = parts[3];
            String quantity = parts[4];
            String price = parts[5];
//            int quantity = Integer.parseInt(parts[4]);
//            double price = Double.parseDouble(parts[5]);
            String buySell = parts[6];

            JSONObject user = usersJson.optJSONObject("user_" + userId);
            if (user == null) {
                user = new JSONObject();
                user.put("user_id", userId);
                user.put("scrips", new JSONObject());
                usersJson.put("user_" + userId, user);
            }

            JSONObject scrips = user.getJSONObject("scrips");
            JSONArray scripArray = scrips.optJSONArray(scrip);
            if (scripArray == null) {
                scripArray = new JSONArray();
                scrips.put(scrip, scripArray);
            }

            JSONObject orderDetails = new JSONObject();
            orderDetails.put("quantity", quantity);
            orderDetails.put("price", price);
            orderDetails.put("exchange", exchange);
            orderDetails.put("order_id", orderId);
            orderDetails.put("Buy/Sell", buySell);

            scripArray.put(orderDetails);
        }
        return usersJson;
    }

    private static void generatePositionBookCsv(LRUCache<String, JSONObject> cache, String customerId) {
        final long startTimeGet = System.nanoTime();
    	JSONObject usersJson = cache.get("user_"+customerId);
    	System.out.println("uj: "+usersJson);
    	final long elapsedTimeGet = System.nanoTime()-startTimeGet;
    	System.out.println("Get time -------> " + elapsedTimeGet + " ns");
        if (usersJson == null ) {
            System.out.println("Customer data not found in cache.");
        }

        JSONObject customerData = (JSONObject) usersJson;
        JSONObject scrips = customerData.getJSONObject("scrips");

        String fileName = "C:\\Users\\Himanshu Kakwani\\OneDrive\\Desktop\\Himanshu Kakwani\\projects\\caching\\PositionBook_" + customerId + ".csv";

        try (FileWriter writer = new FileWriter(fileName)) {
            // Write CSV header
            writer.write("ScripCode,PosType,BuyQty,AvgBuyRate,SellQty,AvgSellRate,NetQty,AvgPrice,BuyAmt,SellAmt,P/L\n");

            for (Object scripObj : scrips.keySet()) {
            	String scrip = (String) scripObj;
                JSONArray orders = scrips.getJSONArray(scrip);

                int buyQty = 0;
                int sellQty = 0;
                double buyAmt = 0.0;
                double sellAmt = 0.0;

                for (int i = 0; i < orders.length(); i++) {
                    JSONObject order = orders.getJSONObject(i);
                    int quantity = order.getInt("quantity");
                    double price = order.getDouble("price");
                    String buySell = order.getString("Buy/Sell");

                    if (buySell.equals("B")) {
                        buyQty += quantity;
                        buyAmt += quantity * price;
                    } else if (buySell.equals("S")) {
                        sellQty += quantity;
                        sellAmt += quantity * price;
                    }
                }

                // Calculate averages
                double avgBuyRate = buyQty > 0 ? buyAmt / buyQty : 0;
                double avgSellRate = sellQty > 0 ? sellAmt / sellQty : 0;

                // Calculate other values
                int netQty = Math.abs(buyQty - sellQty);
                double avgPrice = buyQty > 0 ? avgBuyRate : 0;
                double pl = buyAmt-sellAmt;

                // Write data for each scrip to CSV
                writer.write(String.format("%s,Open,%d,%.2f,%d,%.2f,%d,%.2f,%.2f,%.2f,%.2f\n",
                        scrip.toUpperCase(),
                        buyQty,
                        avgBuyRate,
                        sellQty,
                        avgSellRate,
                        netQty,
                        avgPrice,
                        buyAmt,
                        sellAmt,
                        pl
                ));
            }

            System.out.println("Position Book for customer " + customerId + " has been written to " + fileName);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}

