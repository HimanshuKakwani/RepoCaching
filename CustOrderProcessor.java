package caching;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class CustOrderProcessor {

    // LRU Cache with LinkedHashMap
    static class LRUCache<K, V> extends LinkedHashMap<K, V> {
        private final int maxEntries;

        public LRUCache(int maxEntries) {
            super(maxEntries , 0.9f, true);
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
            "1|tcs|bse|004|12|16.0|B"
        };
        
        // Initialize LRU cache with a max size of 10
        LRUCache<String, JSONObject> cache = new LRUCache<>(10);

        // Process orders to JSON format
        JSONObject usersJson = processOrdersToJson(orders);
        
        // Store in LRU cache
        cache.put("user_data", usersJson);

        // Take customer ID as input
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter customer ID to retrieve data: ");
        String customerId = scanner.nextLine();
        scanner.close();

        // Retrieve and write data for the specified customer
        retrieveAndWriteCustomerData(cache, customerId);
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
                user.put("user_id", Integer.parseInt(userId));
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

    private static void retrieveAndWriteCustomerData(LRUCache<String, JSONObject> cache, String customerId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        JSONObject usersJson = cache.get("user_data");
        if (usersJson == null || !usersJson.has("user_" + customerId)) {
            System.out.println("Customer data not found in cache.");
            return;
        }

        JSONObject customerData = usersJson.getJSONObject("user_" + customerId);
        String fileName = "C:\\Users\\Himanshu Kakwani\\OneDrive\\Desktop\\Himanshu Kakwani\\projects\\caching\\order_book_" + customerId + ".csv";

        try (FileWriter writer = new FileWriter(fileName)) {
            // Write CSV header
            writer.write("ScripCode,Exchange,OrderId,OrderType,B/S,OrderTime,OrderStatus,OrderQty,OrderPrice\n");

            JSONObject scrips = customerData.getJSONObject("scrips");
            for (Object scripObj : scrips.keySet()) {
            	String scrip = (String) scripObj;
                JSONArray orders = scrips.getJSONArray(scrip);

                for (int i = 0; i < orders.length(); i++) {
                    JSONObject order = orders.getJSONObject(i);
                    String orderTime = LocalDateTime.now().format(formatter);

                    // Write each order as a CSV row
                    writer.write(String.format("%s,%s,%s,NOR,%s,%s,Placed,%s,%s\n",
                            scrip.toUpperCase(),
                            order.getString("exchange").toUpperCase(),
                            order.getString("order_id"),
                            order.getString("Buy/Sell"),
                            orderTime,
                            order.getInt("quantity"),
                            order.getDouble("price")));
                }
            }

            System.out.println("Data for customer " + customerId + " is in " + fileName);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}
