package ru.yandex.praktikum.login;

import io.qameta.allure.Epic;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.entity.Courier;
import ru.yandex.praktikum.api.CourierClient;
import ru.yandex.praktikum.entity.CourierCredentials;
import ru.yandex.praktikum.utils.GenerateCourier;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Epic("Login courier")
public class CourierLoginNonExistLoginAndPasswordTest {
    private static final String MESSAGE_NOT_FOUND = "Учетная запись не найдена";
    private CourierClient courierClient;
    private Courier courier;

    @Before
    public void setUp() {
        courier = GenerateCourier.getRandomCourier();
        courierClient = new CourierClient();
    }

    @Test
    @DisplayName("Logging courier with non-existent login and password")
    public void courierLoginNonExistLoginAndPassword() {
        ValidatableResponse response = courierClient.loginCourier(new CourierCredentials(courier.getLogin(), courier.getPassword()));
        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");

        assertThat("Code not equal", statusCode, equalTo(SC_NOT_FOUND));
        assertThat("Message not equal", message, equalTo(MESSAGE_NOT_FOUND));
    }
}
