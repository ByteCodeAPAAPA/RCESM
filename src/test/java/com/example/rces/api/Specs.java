package com.example.rces.api;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static com.example.rces.config.Properties.PROPERTIES;
import static com.example.rces.utils.CustomApiListener.allureRequest;
import static org.hamcrest.Matchers.equalTo;

public class Specs {

    public static RequestSpecification request() {
        return allureRequest()
                .baseUri(PROPERTIES.getBaseUrl())
                .contentType(ContentType.JSON);
    }

    public static ResponseSpecification notFoundResponse() {
        return new ResponseSpecBuilder()
                .expectStatusCode(404)
                .expectBody("error", equalTo("RESOURCE_NOT_FOUND"))
                .build();
    }

}
