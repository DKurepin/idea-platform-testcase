package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode rootNode = mapper.readTree(new File("/Users/DNK/Desktop/Algorithms/src/main/java/org/example/tickets.json"));
            JsonNode ticketsNode = rootNode.get("tickets");

            Map<String, Long> minFlightDuration = calculateMinFlightDuration(ticketsNode);

            int priceDifference = calculatePriceDifference(ticketsNode);

            System.out.println("Minimum flight duration for each carrier:");
            for (Map.Entry<String, Long> entry : minFlightDuration.entrySet()) {
                System.out.println("Carrier: " + entry.getKey() + ", Min Duration: " + entry.getValue() + " minutes");
            }

            System.out.println("\nDifference between average price and median price: " + priceDifference);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Map<String, Long> calculateMinFlightDuration(JsonNode ticketsNode) {
        Map<String, Long> minFlightDuration = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

        for (JsonNode ticketNode : ticketsNode) {
            String origin = ticketNode.get("origin").asText();
            String destination = ticketNode.get("destination").asText();
            if ("VVO".equals(origin) && "TLV".equals(destination)) {
                String carrier = ticketNode.get("carrier").asText();
                String departureDateTime = normalizeDateTime(ticketNode.get("departure_date").asText(), ticketNode.get("departure_time").asText());
                String arrivalDateTime = normalizeDateTime(ticketNode.get("arrival_date").asText(), ticketNode.get("arrival_time").asText());

                ZonedDateTime departure = LocalDateTime.parse(departureDateTime, formatter).atZone(ZoneId.of("Asia/Vladivostok"));

                ZonedDateTime arrival = LocalDateTime.parse(arrivalDateTime, formatter).atZone(ZoneId.of("Asia/Jerusalem"));

                long duration = Duration.between(departure, arrival).toMinutes();

                if (!minFlightDuration.containsKey(carrier) || duration < minFlightDuration.get(carrier)) {
                    minFlightDuration.put(carrier, duration);
                }
            }
        }
        return minFlightDuration;
    }

    private static String normalizeDateTime(String date, String time) {
        String[] timeParts = time.split(":");
        if (timeParts[0].length() == 1) {
            time = "0" + time;
        }
        return date + " " + time;
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
