package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode rootNode = mapper.readTree(new File("/Users/DNK/Desktop/Algorithms/src/main/java/org/example/tickets.json"));
            JsonNode ticketsNode = rootNode.get("tickets");

            Map<String, Integer> minFlightDuration = calculateMinFlightDuration(ticketsNode);

            int priceDifference = calculatePriceDifference(ticketsNode);

            System.out.println("Minimum flight duration for each carrier:");
            for (Map.Entry<String, Integer> entry : minFlightDuration.entrySet()) {
                System.out.println("Carrier: " + entry.getKey() + ", Min Duration: " + entry.getValue() + " minutes");
            }

            System.out.println("\nDifference between average price and median price: " + priceDifference);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Map<String, Integer> calculateMinFlightDuration(JsonNode ticketsNode) {
        Map<String, Integer> minFlightDuration = new HashMap<>();
        for (JsonNode ticketNode : ticketsNode) {
            String origin = ticketNode.get("origin").asText();
            String destination = ticketNode.get("destination").asText();
            int duration = calculateDuration(ticketNode.get("departure_time").asText(), ticketNode.get("arrival_time").asText());

            if ("VVO".equals(origin) && "TLV".equals(destination)) {
                String carrier = ticketNode.get("carrier").asText();
                if (!minFlightDuration.containsKey(carrier) || duration < minFlightDuration.get(carrier)) {
                    minFlightDuration.put(carrier, duration);
                }
            }
        }
        return minFlightDuration;
    }

    private static int calculateDuration(String departureTime, String arrivalTime) {
        String[] depTime = departureTime.split(":");
        String[] arrTime = arrivalTime.split(":");
        int depHour = Integer.parseInt(depTime[0]);
        int depMinute = Integer.parseInt(depTime[1]);
        int arrHour = Integer.parseInt(arrTime[0]);
        int arrMinute = Integer.parseInt(arrTime[1]);
        return (arrHour - depHour) * 60 + (arrMinute - depMinute);
    }

    private static int calculatePriceDifference(JsonNode ticketsNode) {
        List<Integer> prices = new ArrayList<>();
        for (JsonNode ticketNode : ticketsNode) {
            String origin = ticketNode.get("origin").asText();
            String destination = ticketNode.get("destination").asText();
            if ("VVO".equals(origin) && "TLV".equals(destination)) {
                prices.add(ticketNode.get("price").asInt());
            }
        }
        int[] pricesArray = prices.stream().mapToInt(Integer::intValue).toArray();
        Arrays.sort(pricesArray);
        double average = prices.stream().mapToInt(Integer::intValue).average().orElse(0);
        int median;
        if (pricesArray.length % 2 == 0) {
            median = (pricesArray[pricesArray.length / 2] + pricesArray[pricesArray.length / 2 - 1]) / 2;
        } else {
            median = pricesArray[pricesArray.length / 2];
        }
        return (int) (average - median);
    }
}
