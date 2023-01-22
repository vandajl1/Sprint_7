package ru.yandex.praktikum.api;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import ru.yandex.praktikum.config.ScooterConfig;
import ru.yandex.praktikum.entity.Courier;
import ru.yandex.praktikum.entity.CourierCredentials;
import ru.yandex.praktikum.utils.EndPoints;
import static io.restassured.RestAssured.given;

public class CourierClient extends ScooterConfig {
    @Step("Send POST request to /api/v1/courier/login")
    public ValidatableResponse loginCourier(CourierCredentials credentials) {
        return given()
                .spec(getBaseSpec())
                .body(credentials)
                .log().all()
                .post(EndPoints.COURIER_PATH + "login")
                .then()
                .log().all();
    }

    @Step("Send POST request to /api/v1/courier")
    public ValidatableResponse createCourier(Courier courier) {
        return given()
                .spec(getBaseSpec())
                .body(courier)
                .log().all()
                .post(EndPoints.COURIER_PATH)
                .then()
                .log().all();
    }

    @Step("Send DELETE request to /api/v1/courier/{id}")
    public ValidatableResponse deleteCourier(Integer id) {
        return given()
                .spec(getBaseSpec())
                .log().all()
                .delete(EndPoints.COURIER_PATH + id)
                .then()
                .log().all();
    }

    @Step("Send DELETE request to /api/v1/courier")
    public ValidatableResponse deleteCourier() {
        return given()
                .spec(getBaseSpec())
                .log().all()
                .delete(EndPoints.COURIER_PATH)
                .then()
                .log().all();
    }
}
