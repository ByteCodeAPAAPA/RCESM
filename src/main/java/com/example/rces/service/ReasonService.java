package com.example.rces.service;

import com.example.rces.dto.ReasonDto;

import java.util.List;

public interface ReasonService {

    void createOrUpdateReason(String reasonText, String requestType);

    List<ReasonDto> getAllReasons();

    List<ReasonDto> searchReasons(String search);
}
