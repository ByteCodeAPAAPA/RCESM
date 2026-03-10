package com.example.rces.utils;

import com.example.rces.models.Requests;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class DecimalUtil {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public static List<Integer> countDailyRequestsList(List<Requests> filteredRequests) {
        int[] dailyCounts = countDailyRequests(filteredRequests);
        return Arrays.stream(dailyCounts).boxed().toList();
    }

    public static int[] countDailyRequests(List<Requests> requests) {
        int daysInMonth = LocalDate.now().getDayOfMonth();
        int[] dailyCounts = new int[daysInMonth];

        for (Requests req : requests) {
            int day = req.getCreatedDate().atZone(ZoneId.systemDefault()).toLocalDate().getDayOfMonth();
            dailyCounts[day - 1]++;
        }
        return dailyCounts;
    }

    //Вычисление среднего времени обработки заявки в часах
    public static Map<String, Double> averageTimeRequests(List<Requests> requests) {
        Map<String, Double> mapAverageTime = new HashMap<>();

        for (String key : Arrays.stream(Requests.Type.values()).map(Enum::name).toList()) {
            long requestsCount = 0;
            long time = 0;

            Requests.Type type = Requests.Type.valueOf(key);

            for (Requests req : requests) {
                if (req.getDateWork() != null && req.getTypeRequest().equals(type)) {
                    Duration duration = Duration.between(req.getCreatedDate(), req.getDateWork());
                    time += duration.toMinutes();
                    requestsCount++;
                }
            }

            Double averageTime = (requestsCount > 0) ? (double) (time / requestsCount) / 60 : 0.0;
            mapAverageTime.put(key, averageTime);
        }

        return mapAverageTime;
    }

    //Вычисление среднего времени закрытия заявки в часах
    public static Map<String, Double> averageClosedRequests(List<Requests> requests) {
        Map<String, Double> mapAverageClosed = new HashMap<>();

        for (String key : Arrays.stream(Requests.Type.values()).map(Enum::name).toList()) {
            long requestsCount = 0;
            long time = 0;
            Requests.Type type = Requests.Type.valueOf(key);

            for (Requests req : requests) {
                if (req.getDateWork() != null && req.getTypeRequest().equals(type)) {
                    Duration duration = Duration.between(req.getDateWork(), req.getCloseDate());
                    time += duration.toMinutes();
                    requestsCount++;
                }
            }
            double averageTimeRequest = (requestsCount > 0) ? (double) (time / requestsCount) / 60 : 0.0;
            mapAverageClosed.put(key, averageTimeRequest);
        }
        return mapAverageClosed;
    }

    public static Map<String, List<Integer>> getCountDays(List<Requests> requests) {
        Map<String, List<Integer>> map = new HashMap<>();
        for (String type : Arrays.stream(Requests.Type.values()).map(Enum::name).toList()) {
            List<Requests> requestsOfType = requests
                    .stream()
                    .filter(r -> r.getTypeRequest().equals(Requests.Type.valueOf(type)))
                    .toList();
            List<Requests> filterRequests = filterRequestsByCurrentMonth(requestsOfType, LocalDate.now());
            int[] dailyCounts = countDailyRequests(filterRequests);
            map.put(type, Arrays.stream(dailyCounts).boxed().toList());
        }
        return map;
    }

    public static List<Requests> filterRequestsByCurrentMonth(List<Requests> requests, LocalDate now) {
        return requests.stream()
                .filter(req -> {
                    LocalDate createdDate = req.getCreatedDate().atZone(ZoneId.systemDefault()).toLocalDate();
                    return createdDate.getMonth() == now.getMonth() &&
                            createdDate.getYear() == now.getYear();
                })
                .toList();
    }

    public static Map<String, List<List<Map<String, String>>>> getAllRequestsByTypes(
            List<Requests> requestsFilterDate,
            List<String> chartDates,
            String... types) {

        Map<String, Map<String, List<Requests>>> groupedByDate = new HashMap<>();

        for (Requests request : requestsFilterDate) {
            String date = formatDate(request.getCreatedDate());
            String type = request.getTypeRequest().getName();

            groupedByDate
                    .computeIfAbsent(date, k -> new HashMap<>())
                    .computeIfAbsent(type, k -> new ArrayList<>())
                    .add(request);
        }

        Map<String, List<List<Map<String, String>>>> result = new HashMap<>();

        for (String type : types) {
            List<List<Map<String, String>>> typeRequests = new ArrayList<>();

            for (String date : chartDates) {
                Map<String, List<Requests>> dayData = groupedByDate.getOrDefault(date, Collections.emptyMap());
                List<Requests> dayRequests = dayData.getOrDefault(type, Collections.emptyList());

                List<Map<String, String>> mappedRequests = dayRequests.stream()
                        .map(DecimalUtil::mapRequestToDto)
                        .collect(Collectors.toList());

                typeRequests.add(mappedRequests);
            }

            result.put(type, typeRequests);
        }

        return result;
    }

    private static Map<String, String> mapRequestToDto(Requests request) {
        Map<String, String> dto = new HashMap<>();
        dto.put("number", String.valueOf(request.getRequestNumber()));
        dto.put("id", String.valueOf(request.getId()));
        dto.put("title", request.getTitle() != null ? request.getTitle() : "");
        dto.put("status", request.getStatus() != null ? request.getStatus().getName() : "");
        return dto;
    }

    private static String formatDate(Instant instant) {
        if (instant == null) return "";
        LocalDate createdDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
        return createdDate.format(DATE_FORMATTER);
    }

    public static Map<String, Integer> countRequest(List<Requests> filteredRequests) {
        Map<String, Integer> qtuRequests = new HashMap<>();
        List<Requests> requests;
        for (String type : Arrays.stream(Requests.Type.values()).map(Enum::name).toList()) {
            Requests.Type reqType = Requests.Type.valueOf(type);
            requests = filteredRequests.stream()
                    .filter(req -> req.getTypeRequest() == reqType)
                    .toList();
            qtuRequests.put(type, requests.size());
        }
        return qtuRequests;
    }

}
