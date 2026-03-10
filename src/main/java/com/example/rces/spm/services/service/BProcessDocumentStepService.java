package com.example.rces.spm.services.service;

//Создание Документа "Комплектация ПрП" для формы комплектация ДДЦ
// и списание строки с нужным заходом и переводом шага на 60

import com.example.rces.service.TokenService;
import com.example.rces.spm.models.*;
import com.example.rces.spm.services.SPMService;
import com.jayway.jsonpath.JsonPath;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class BProcessDocumentStepService {

    private final SPMService service;
    private final TokenService tokenService;
    private HttpEntity<String> entity;

    @Autowired
    public BProcessDocumentStepService(SPMService service, TokenService tokenService) {
        this.service = service;
        this.tokenService = tokenService;
    }

    private String requestBody = "{}";

    @PostConstruct
    public void init() {
        this.entity = new HttpEntity<>(requestBody, getHeaders());
    }

    private Integer bprocessId;

    private Long employeeId;

    private Long jobId;

    String login = "admin";

    String password = "123456";

    public HttpHeaders getHeaders() {
        String authStr = login + ":" + password;
        byte[] encodedAuth = Base64.getEncoder().encode(authStr.getBytes(StandardCharsets.UTF_8));
        String authHeader = "Basic " + new String(encodedAuth);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    public void createDoc(Long id) throws IOException, InterruptedException {
        jobId = id;

        JobStep jobStep = service.findById(JobStep.class, id);
        StockNode stockNode = service.findById(StockNode.class, jobStep.getMlmNode().getReleaseStockNode().getId());
        PrimaryDemand primaryDemand = service.findById(PrimaryDemand.class, jobStep.getJobcomponent().getPrimarydemand().getId());

        List<Employee2Warehouse> employee2Warehouses = service.findAll(Employee2Warehouse.class).stream()
                .filter(employee2Warehouse -> employee2Warehouse.getWarehouse().getId().equals(stockNode.getId()))
                .toList();

        if (!employee2Warehouses.isEmpty()) {
            employeeId = employee2Warehouses.get(0).getEmployee().getId();
        }

        String url = "http://localhost:8080/api/BProcessDocument/load";

        Map<String, Object> data = new HashMap<>();
        data.put("id", 0);
        data.put("bprocess_id", 29894);
        data.put("str_code", 1);
        data.put("date_document", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        data.put("mlm_node_id", jobStep.getMlmNode().getId());
        data.put("stock_node_id", stockNode.getId());
        data.put("primarydemand_list", primaryDemand.getId());
        data.put("employee_id", employeeId);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + tokenService.getToken("api", "123456"));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(data, headers);

        RestTemplate restTemplate = new RestTemplate();

        String response = restTemplate.postForObject(url, request, String.class);
        bprocessId = JsonPath.read(response, "$.id");
        handleStepbox30();
        deleteDoc();
        handleStepbox60();
        System.out.println("Response: " + response);
    }

    public void handleStepbox30() {
        String url = "http://localhost:8080/stepbox/act/scmo_bpm/BProcessDocument/stepbox/" + bprocessId + "/" + 30528;

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    public void deleteDoc() {
        String url = "http://localhost:8080/bprocess-doc/" + bprocessId + "/" + jobId;

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);

        ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    public void handleStepbox60() {
        String url = "http://localhost:8080/stepbox/act/scmo_bpm/BProcessDocument/stepbox/" + bprocessId + "/" + 30609;

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    public List componentList() {
        return service.getEntityManager()
                .createNativeQuery("SELECT * FROM jm_shift_task_line" +
                                " WHERE CAST(created_at AS DATE) = date(' " + LocalDate.now() + "')",
                        ShiftTaskLine.class)
                .getResultList();
    }

    public List componentFinishedList() {
        return service.getEntityManager()
                .createNativeQuery(
                        "SELECT * FROM jm_shift_task_line " +
                                "WHERE (CAST(created_at AS DATE) = date(' " + LocalDate.now() + "') AND qty_finished >= qty_production)",
                        ShiftTaskLine.class)
                .getResultList();
    }

    public List getMlmNodeList() {
        return service.findAll(MlmNode.class);
    }
}
