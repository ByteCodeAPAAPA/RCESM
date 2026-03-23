package com.example.rces.data;

import com.example.rces.api.test.BaseApiTest;
import com.example.rces.dto.SubDivisionDTO;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class SubDivisionTestHelper {
    private static List<SubDivisionDTO> cachedSubDivisions;
    private static Instant cacheTimestamp;
    private static final Duration CACHE_TTL = Duration.ofMinutes(5);


    public static List<SubDivisionDTO> getAllSubDivisions() {
        if (cachedSubDivisions != null &&
                cacheTimestamp != null &&
                Duration.between(cacheTimestamp, Instant.now()).compareTo(CACHE_TTL) < 0) {
            return cachedSubDivisions;
        }

        try {
            List<SubDivisionDTO> divisions = given()
                    .spec(BaseApiTest.getAuthorizedRequestSpec())
                    .basePath("/api/sub-divisions")
                    .get()
                    .then()
                    .log().ifError()
                    .statusCode(200)
                    .extract()
                    .jsonPath()
                    .getList("", SubDivisionDTO.class);

            assertThat(divisions)
                    .as("Список подразделений не должен быть пустым")
                    .isNotEmpty();

            cachedSubDivisions = divisions;
            cacheTimestamp = Instant.now();

            log.info("Загружено {} подразделений", divisions.size());
            return divisions;

        } catch (Exception e) {
            log.error("Ошибка при получении списка подразделений", e);
            throw new RuntimeException("Ошибка при получении списка подразделений", e);
        }
    }

    public static SubDivisionDTO getRandomSubDivision() {
        List<SubDivisionDTO> divisions = getAllSubDivisions();
        int randomIndex = ThreadLocalRandom.current().nextInt(divisions.size());
        SubDivisionDTO selected = divisions.get(randomIndex);
        log.debug("Выбрано случайное подразделение: {}", selected.getName());
        return selected;
    }

    public static Stream<String> getRandomSubDivisionNameStream() {
        return Stream.of(getRandomSubDivision().getName());
    }

    public static Optional<SubDivisionDTO> getSubDivisionByName(String name) {
        log.debug("Поиск подразделения по имени: {}", name);
        return getAllSubDivisions().stream()
                .filter(dto -> dto.getName().equals(name))
                .findFirst();
    }

    public static void clearCache() {
        cachedSubDivisions = null;
        cacheTimestamp = null;
        log.debug("Кэш подразделений сброшен");
    }
}