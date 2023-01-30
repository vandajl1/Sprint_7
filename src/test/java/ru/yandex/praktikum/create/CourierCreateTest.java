package ru.yandex.praktikum.create;

import io.qameta.allure.Epic;
import io.qameta.allure.Issue;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.entity.Courier;
import ru.yandex.praktikum.api.CourierClient;
import ru.yandex.praktikum.entity.CourierCredentials;
import ru.yandex.praktikum.utils.GenerateCourier;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Epic("Create courier")
public class CourierCreateTest {
    private static final String MESSAGE_BAD_REQUEST = "Недостаточно данных для создания учетной записи";
    private static final String MESSAGE_CONFLICT = "Этот логин уже используется. Попробуйте другой.";
    private ValidatableResponse response;
    private CourierClient courierClient;
    private Courier courier;
    private int id;

    @Before
    public void setUp() {
        courier = GenerateCourier.getRandomCourier();
        courierClient = new CourierClient();
    }

    @After
    public void clearState() {
        courierClient.deleteCourier(id);
    }

    @Test
    @DisplayName("Create courier is valid credentials")
    public void courierCreateValidCredentials() {
        response = courierClient.createCourier(courier);
        int statusCode = response.extract().statusCode();
        boolean isCreate = response.extract().path("ok");
        response = courierClient.loginCourier(new CourierCredentials(courier.getLogin(), courier.getPassword()));
        id = response.extract().path("id");

        assertThat("Courier create incorrect", statusCode, equalTo(SC_CREATED));
        assertThat("Courier create incorrect", isCreate, equalTo(true));
    }

    @Test
    @DisplayName("Create courier is empty field password")
    public void courierCreateIsEmptyPassword() {
        courier.setPassword(null);
        response = courierClient.createCourier(courier);
        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");

        assertThat("Not password data to create an courier", statusCode, equalTo(SC_BAD_REQUEST));
        assertThat("Message not equal", message,  equalTo(MESSAGE_BAD_REQUEST));
    }

    @Test
    @DisplayName("Create courier is empty field login")
    public void courierCreateIsEmptyLogin() {
        courier.setPassword(null);
        response = courierClient.createCourier(courier);
        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");

        assertThat("Not login data to create an courier", statusCode, equalTo(SC_BAD_REQUEST));
        assertThat("Message not equal", message,  equalTo(MESSAGE_BAD_REQUEST));
    }

    @Test
    @DisplayName("Repeated request by duplicate data")
    @Issue("BUG-Message not equal")
    public void courierCreateRepeatedRequestByDuplicateData() {
        courierClient.createCourier(courier);
        response = courierClient.createCourier(courier);
        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");

        assertThat("Code not equal", statusCode, equalTo(SC_CONFLICT));
        assertThat("Message not equal", message,  equalTo(MESSAGE_CONFLICT));
    }
}
