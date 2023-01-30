package ru.yandex.praktikum.delete;

import java.util.Random;
import io.qameta.allure.Epic;
import io.qameta.allure.Issue;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.entity.Courier;
import ru.yandex.praktikum.api.CourierClient;
import ru.yandex.praktikum.entity.CourierCredentials;
import ru.yandex.praktikum.utils.GenerateCourier;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Epic("Delete courier")
public class CourierDeleteTest {
    private static final String MESSAGE_BAD_REQUEST = "Not Found.";
    private static final String MESSAGE_NOT_FOUND = "Курьера с таким id нет.";
    private ValidatableResponse response;
    private CourierClient courierClient;
    private Courier courier;

    @Before
    public void setUp() {
        courier = GenerateCourier.getRandomCourier();
        courierClient = new CourierClient();
        courierClient.createCourier(courier);
    }

    @Test
    @DisplayName("Delete courier is valid credentials")
    public void courierDeleteValidCredentials() {
        response = courierClient.loginCourier(new CourierCredentials(courier.getLogin(), courier.getPassword()));
        int id = response.extract().path("id");
        response = courierClient.deleteCourier(id);
        int statusCode = response.extract().statusCode();
        boolean isDelete = response.extract().path("ok");

        assertThat("Code not equal", statusCode, equalTo(SC_OK));
        assertThat("Message not equal", isDelete, equalTo(true));
    }

    @Test
    @DisplayName("Delete courier, none id")
    @Issue("BUG-Code not equal")
    public void courierDeleteNonId() {
        response = courierClient.deleteCourier();
        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");

        assertThat("Code not equal", statusCode, equalTo(SC_NOT_FOUND));
        assertThat("Message not equal", message, equalTo(MESSAGE_BAD_REQUEST));
    }

    @Test
    @DisplayName("Delete courier by random id")
    @Issue("BUG-Message not equal")
    public void courierDeleteByRandomId() {
        response = courierClient.deleteCourier(new Random().nextInt());
        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");

        assertThat("Code not equal", statusCode, equalTo(SC_NOT_FOUND));
        assertThat("Message not equal", message, equalTo(MESSAGE_NOT_FOUND));
    }
}
