package com.example.rces.service;

import com.example.rces.dto.*;
import com.example.rces.models.SPE;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

public interface SpeService {

    SpeDTO createSPE(SpeFgisCreateDTO dto);

    SPE findByNumber(Integer number);

    SpeDTO createSPE(SpeCreateDTO dto);

    List<SpeDTO> getAllSPE();

    SpeDTO updateSPE(Integer number, Long version, Map<String, Object> changes);

    DocumentDTO createSpeDocument(SPE spe, DocumentCreateDTO dto, Object file);

    void setOrganizationWithFgis(SPE spe, JsonNode data);

    void deleteSpe(Integer number);

    List<SPE> findAllByIdList(List<Integer> ids);

    void calculateDateVerification();
}
