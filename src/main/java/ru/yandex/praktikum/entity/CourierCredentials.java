package ru.yandex.praktikum.entity;

import io.qameta.allure.internal.shadowed.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CourierCredentials {
    private String login;
    private String password;

    public CourierCredentials() {

    }
}
