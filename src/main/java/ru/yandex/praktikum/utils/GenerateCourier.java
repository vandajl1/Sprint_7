package ru.yandex.praktikum.utils;

import io.qameta.allure.Allure;
import org.apache.commons.lang3.RandomStringUtils;
import ru.yandex.praktikum.entity.Courier;

public class GenerateCourier {
    public static Courier getRandomCourier() {
        String login = RandomStringUtils.randomAlphanumeric(10);
        String password = RandomStringUtils.randomNumeric(10);
        String firstName = RandomStringUtils.randomAlphabetic(10);

        Allure.addAttachment("Login : ", login);
        Allure.addAttachment("Password : ", password);
        Allure.addAttachment("First name : ", firstName);

        return new Courier(login, password, firstName);
    }
}
