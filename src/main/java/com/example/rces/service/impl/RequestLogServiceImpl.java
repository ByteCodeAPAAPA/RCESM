package com.example.rces.service.impl;

import com.example.rces.payload.LogPayload;
import com.example.rces.utils.AppUtil;
import com.example.rces.models.Employee;
import com.example.rces.models.RequestLog;
import com.example.rces.models.Requests;
import com.example.rces.repository.RequestLogRepository;
import com.example.rces.service.RequestLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.example.rces.utils.ServiceUtil.getMetadata;

@Service
@Transactional(transactionManager = "primaryTransactionManager")
public class RequestLogServiceImpl implements RequestLogService {

    private final RequestLogRepository repository;

    @Autowired
    public RequestLogServiceImpl(RequestLogRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<RequestLog> createLog(Requests oldRequest, Requests newRequest, Employee updaterUser) {
        Set<String> ignoredFields = Set.of(
                "id", "version", "updateDate", "closeDate", "dateWork", "log", "typeRequest", "messageId",
                "createdBy", "updateBy", "closedEmployee", "images", "chatId");
        Map<String, String> metadata = getMetadata(oldRequest.getClass(), ignoredFields, oldRequest, newRequest);
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        List<RequestLog> logs = repository.findAllByRequest(oldRequest)
                .stream()
                .filter(log -> log.getDate().equals(now)).toList();
        if (!metadata.isEmpty()) {
            if (logs.isEmpty()) {
                RequestLog log = new RequestLog(newRequest, updaterUser, metadata);
                if (!metadata.containsKey("Статус")) {
                    AppUtil.setBool(true);
                }
                return List.of(log);
            }
            for (RequestLog log : logs) {
                log.addToMetadata(metadata);
            }
            if (!metadata.containsKey("Статус")) {
                AppUtil.setBool(true);
            }
        }
        return logs;
    }

    @Override
    public List<LogPayload> getAllByRequestId(UUID requestId) {
        return repository.findAllByRequestId(requestId)
                .stream()
                .map(log -> new LogPayload(log.getDate(), log.getUser().getName(), log.getMetadata()))
                .sorted(Comparator.comparing(LogPayload::date))
                .toList();
    }
}