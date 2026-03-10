package com.example.rces.models.enums;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public enum Inconsistency {

    Inconsistency1("Несоответствие геометрии", "ВИК"),
    Inconsistency2("Наружный дефект; сварные соединения", "ВИК"),
    Inconsistency3("Несоответствие чертежу", "ВИК"),
    Inconsistency4("Несоответствие консервации", "ВИК"),
    Inconsistency5("Несоответствие качеств уплотнительной поверхности", "ВИК"),
    Inconsistency6("Несоответствие материала", "ЛНК"),
    Inconsistency7("Дефекты сварочных соединений", "ЛНК"),
    Inconsistency8("Несоответствие документации", "ВИК"),
    Inconsistency9("Не пройдено гидроиспытание", "ВИК");

    private final String name;

    private final String controlType;

    Inconsistency(String name, String controlType) {
        this.name = name;
        this.controlType = controlType;
    }

    public String getName() {
        return name;
    }

    public String getControlType() {
        return controlType;
    }

    public static Set<Inconsistency> fromField(Object field) {
        try {
            String input = field.toString();
            Pattern pattern = Pattern.compile("\"([^\"]+)\"");
            Matcher matcher = pattern.matcher(input);

            Set<String> result = new HashSet<>();
            while (matcher.find()) {
                String match = matcher.group(1);
                if (!match.trim().equals("name") && !match.trim().isEmpty()) {
                    result.add(match);
                }
            }
            return Arrays.stream(Inconsistency.values())
                    .filter(inc -> result.contains(inc.getName()))
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}


