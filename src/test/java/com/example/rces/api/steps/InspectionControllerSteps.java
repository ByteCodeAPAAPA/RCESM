package com.example.rces.api.steps;

import com.example.rces.api.test.BaseApiTest;
import com.example.rces.dto.InspectionCreateDTO;
import com.example.rces.dto.InspectionDTO;
import com.example.rces.dto.InspectionViolationCreateDTO;
import com.example.rces.dto.InspectionViolationDTO;
import io.qameta.allure.Step;
import io.restassured.builder.MultiPartSpecBuilder;

import java.util.UUID;

import static io.restassured.RestAssured.given;

public class InspectionControllerSteps {

    private static final String PATH = "/api/inspection/";

    @Step("Создать тестовую инспекцию")
    public static InspectionDTO createInspection(InspectionCreateDTO createDTO) {
        return given()
                .spec(BaseApiTest.getAuthorizedRequestSpec())
                .basePath(PATH + "create-inspection")
                .post()
                .then().log().all()
                .statusCode(200)
                .extract().as(InspectionDTO.class);
    }

    @Step("Создать критерий для инспекции")
    public static InspectionViolationDTO createViolation(InspectionViolationCreateDTO violationCreateDTO) {
        return given()
                .spec(BaseApiTest.getAuthorizedRequestSpec())
                .basePath(PATH + "create-violation")
                .contentType("multipart/form-data")
                .multiPart(new MultiPartSpecBuilder(violationCreateDTO)
                        .controlName("data")
                        .mimeType("application/json")
                        .charset("UTF-8")
                        .build())
                .post()
                .then().log().all()
                .extract().as(InspectionViolationDTO.class);
    }

    @Step("Изменить статус критерия")
    public static String changeViolationStatus(UUID id) {
        return given()
                .spec(BaseApiTest.getAuthorizedRequestSpec())
                .basePath(PATH + "change-status-violation/{id}")
                .pathParam("id", id)
                .patch()
                .then().log().all()
                .statusCode(200)
                .extract().as(String.class);
    }

    @Step("Создать повторную проверку инспекции")
    public static InspectionDTO createSecondaryInspection(Integer inspectionId) {
        return given()
                .spec(BaseApiTest.getAuthorizedRequestSpec())
                .basePath(PATH + "create-secondary-inspection/{inspectionId}")
                .pathParam("inspectionId", inspectionId)
                .post()
                .then().log().all()
                .statusCode(200)
                .extract().as(InspectionDTO.class);
    }

    @Step("Удалить критерий у инспекции")
    public static void deleteViolation(UUID violationId, Integer statusCode) {
        given()
                .spec(BaseApiTest.getAuthorizedRequestSpec())
                .basePath(PATH + "delete-violation/{id}")
                .pathParam("id", violationId)
                .delete()
                .then().log().all()
                .statusCode(statusCode);
    }

    @Step("Удалить инспекцию")
    public static void deleteInspection(Integer inspectionId, Integer statusCode) {
        given()
                .spec(BaseApiTest.getAuthorizedRequestSpec())
                .basePath(PATH + "delete-inspection/{id}")
                .pathParam("id", inspectionId)
                .delete()
                .then().log().all()
                .statusCode(statusCode);
    }


}
