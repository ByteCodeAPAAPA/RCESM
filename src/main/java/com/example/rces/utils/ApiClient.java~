package com.example.rces.utils;

import com.example.rces.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Component
public class ApiClient {

    private final RestTemplate restTemplate;

    @Autowired
    public ApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public JsonNode getFgisData(String miNumber) {
        String fgisURL = "https://fgis.gost.ru/fundmetrology/eapi/vri";
        var responseForId = restTemplate.getForObject(
                fgisURL + "?mi_number={mi_number}&rows=1&org_title=ФБУ \"ВОРОНЕЖСКИЙ ЦСМ\"",
                JsonNode.class, miNumber);
        if (responseForId != null && !responseForId.path("result").path("items").isEmpty()) {
            JsonNode items = responseForId.path("result").path("items");
            for (JsonNode item : items) {
                var vriId = item.path("vri_id").asText();
                var response = restTemplate.getForObject(fgisURL + "/" + vriId, JsonNode.class);
                JsonNode data = Objects.requireNonNull(response).path("result");
                if (!data.path("vriInfo").path("miOwner").asText().equals("Общество с ограниченной ответственностью \"Борисоглебское машиностроение\"")) {
                    continue;
                }
                return data;
            }
            throw new ResourceNotFoundException(String.format("В реестре ФГИС нет СИ с заводским номером %s для БорМаш", miNumber));
        }
        throw new ResourceNotFoundException(String.format("В реестре ФГИС нет СИ с заводским номером %s", miNumber));
    }

    public List<JsonNode> getFgisData(List<String> miNumbers) {
        ExecutorService pool = Executors.newFixedThreadPool(5);
        try {
            List<CompletableFuture<JsonNode>> futures = miNumbers.stream()
                    .map(miNumber -> CompletableFuture.supplyAsync(() -> {
                        try {
                            return getFgisData(miNumber);
                        } catch (Exception e) {
                            return null;
                        }
                    }, pool))
                    .toList();
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            return futures.stream()
                    .map(future -> {
                        try {
                            return future.get();
                        } catch (Exception e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } finally {
            pool.shutdown();
        }
    }

}
