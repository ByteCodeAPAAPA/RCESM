package com.example.rces.web.pages;

import com.codeborne.selenide.Selenide;
import com.google.gson.Gson;
import io.qameta.allure.Step;

public interface IBase {

    @Step("Добавление данных в saveMassive")
    default void addToSaveMassive(String key, Object value) {
        String jsValue;
        if (value == null) {
            jsValue = "null";
        } else if (value instanceof String) {
            String escaped = ((String) value)
                    .replace("\\", "\\\\")
                    .replace("'", "\\'")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r");
            jsValue = "'" + escaped + "'";
        } else if (value instanceof Number || value instanceof Boolean) {
            jsValue = value.toString();
        } else {
            Gson gson = new Gson();
            jsValue = gson.toJson(value);
        }

        Selenide.executeJavaScript(
                String.format("this.saveMassive['%s'] = %s;", key, jsValue)
        );
    }

}
