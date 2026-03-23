package com.example.rces.data;

import com.example.rces.dto.EmployeeDTO;
import net.datafaker.Faker;

import java.util.Locale;

import static com.example.rces.data.SubDivisionTestHelper.empty_subDivision;

public class Employee {

    public static final EmployeeDTO admin_user = new EmployeeDTO(
            1L, "admin", empty_subDivision, "ADMIN", true, -1L
    );

    private static final Faker faker = new Faker(new Locale("en"));

    public static EmployeeDTO employeeGenerator() {
        return EmployeeDTO.builder()
                .name(faker.name().firstName() + " " + faker.name().lastName())
                .subDivision(empty_subDivision)
                .role("USER")
                .isActive(true)
                .chatId(-1L)
                .build();
    }


}
