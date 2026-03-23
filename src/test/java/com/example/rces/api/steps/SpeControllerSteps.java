package com.example.rces.api.steps;

import com.example.rces.api.test.BaseApiTest;
import com.example.rces.dto.SpeCreateDTO;
import com.example.rces.dto.SpeDTO;
import io.qameta.allure.Step;

import static io.restassured.RestAssured.given;

public class SpeControllerSteps {

    private static final String PATH = "/api/spe/";

    @Step("Создать тестовое оборудование")
    public static SpeDTO createSGI(SpeCreateDTO newSPE) {
        return given()
                .spec(BaseApiTest.getAuthorizedRequestSpec())
                .basePath(PATH + "create-spe")
                .body(newSPE)
                .post()
                .then().log().ifError()
                .statusCode(200)
                .extract().as(SpeDTO.class);
    }

}
