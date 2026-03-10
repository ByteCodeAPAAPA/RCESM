package com.example.rces.data;

import com.example.rces.dto.SubDivisionDTO;
import net.datafaker.Faker;

import java.util.Locale;

public class SubDivision {

    public static final SubDivisionDTO empty_subDivision = new SubDivisionDTO(
            27L, "EMPTY", "Заглушка"
    );

    private static final Faker faker = new Faker(new Locale("en"));

    public static SubDivisionDTO subDivisionGenerator() {
        return SubDivisionDTO.builder()
                .code(faker.code().ean13())
                .name(faker.name().firstName() + " " + faker.name().lastName())
                .build();
    }

}
