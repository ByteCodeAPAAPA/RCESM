package com.example.rces.service;

import com.example.rces.models.Employee;
import com.example.rces.models.RequestLog;
import com.example.rces.models.Requests;
import com.example.rces.payload.LogPayload;

import java.util.List;
import java.util.UUID;

public interface RequestLogService {

    List<RequestLog> createLog(Requests oldRequest, Requests newRequest, Employee updaterUser);

    List<LogPayload>getAllByRequestId (UUID requestId);

}
