package com.example.rces.service.impl;


import com.example.rces.controller.payload.ApplicationInfoDTO;
import com.example.rces.controller.payload.FeatureDTO;
import com.example.rces.service.HelpService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class HelpServiceImpl implements HelpService {

    @Override
    public ApplicationInfoDTO getApplicationInfo() {
        ApplicationInfoDTO info = new ApplicationInfoDTO();
        info.setTitle("Система управления заявками");
        info.setDocumentationUrl("/api/docs");

        info.setFeatures(getFeaturesFromConfig());

        return info;
    }

    private List<FeatureDTO> getFeaturesFromConfig() {
        return Arrays.asList(
                new FeatureDTO("Создание заявок", "Возможность создания новых заявок", true, "main"),
                new FeatureDTO("Просмотр и поиск заявок", "Поиск,фильтрация и отслеживание заявок", true, "main"),
                new FeatureDTO("Изменение статуса заявки", "Функционал изменения заявок", true, "main")
        );
    }
}