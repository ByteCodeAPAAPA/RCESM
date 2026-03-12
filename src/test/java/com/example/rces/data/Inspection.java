package com.example.rces.data;

import com.example.rces.dto.InspectionCreateDTO;
import com.example.rces.dto.InspectionViolationCreateDTO;
import com.example.rces.models.InspectionViolation;
import net.datafaker.Faker;
import org.junit.jupiter.params.provider.Arguments;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Inspection {

    public static InspectionCreateDTO createTestInspectionDTO(String subDivision) {
        return InspectionCreateDTO.builder()
                .subDivision(subDivision)
                .type(com.example.rces.models.Inspection.TypeInspection.primary.getName())
                .primaryInspectionId(null)
                .build();
    }

    public static InspectionViolationCreateDTO createTestViolationDTO(Integer inspectionId, String subDivision) {
        return InspectionViolationCreateDTO.builder()
                .inspectionId(inspectionId)
                .description("Тестовое описание")
                .criteria(Arrays.toString(randomCriteria().get()))
                .score(ThreadLocalRandom.current().nextInt(5))
                .subDivision(subDivision)
                .build();
    }

    private static Arguments randomCriteria() {
        Faker faker = new Faker();
        List<InspectionViolation.CriteriaInspection> criteriaList = List.of(InspectionViolation.CriteriaInspection.values());
        return Arguments.of(
                criteriaList.get(ThreadLocalRandom.current().nextInt(criteriaList.size())),
                faker.lorem().sentence()
        );
    }


}
