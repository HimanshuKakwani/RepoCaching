package caching;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;

public class batching {

    public static void main(String[] args) throws Exception {
        String[] messages = {
            "1|itc|nse|001|10|8.2|B", "2|acc|bse|003|2|15.0|S", 
            "1|itc|bse|002|1|8.4|B", "1|tcs|bse|004|12|16.0|B", 
            // Add more messages here...
        };

        InetAddress group = InetAddress.getByName("230.0.0.1");
        int port = 5000;

        try (DatagramSocket socket = new DatagramSocket()) {
            int batchSize = 100;
            for (int i = 0; i < messages.length; i += batchSize) {
                // Send messages in batches
                List<String> batch = List.of(messages).subList(i, Math.min(i + batchSize, messages.length));
                for (String message : batch) {
                    byte[] buffer = message.getBytes();
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, port);
                    socket.send(packet);
                }

                System.out.println("Batch sent. Sleeping for 3 seconds...");
                Thread.sleep(3000); // 3 seconds delay
            }
        }
    }
}

