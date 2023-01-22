package ru.yandex.praktikum.order;

import java.util.Random;
import io.qameta.allure.Epic;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.api.OrderClient;
import ru.yandex.praktikum.entity.Order;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Epic("Get order track number")
public class OrderTrackTest {
    private static final String MESSAGE_BAD_REQUEST = "Недостаточно данных для поиска";
    private static final String MESSAGE_NOT_FOUND = "Заказ не найден";
    private ValidatableResponse response;
    private OrderClient orderClient;
    private int trackNumber;

    @Before
    public void setUp() {
        orderClient = new OrderClient();
        response = orderClient.createOrder(Order.getFinalInstanceOrder());
        trackNumber = response.extract().path("track");
    }

    @Test
    @DisplayName("Get order is valid track number")
    public void getOrderValidTrackNumber() {
        response = orderClient.getOrderByTrackNumber(trackNumber);
        int statusCode = response.extract().statusCode();

        assertThat("Code not equal", statusCode, equalTo(SC_OK));
        assertThat("Track number is empty", trackNumber, notNullValue());
    }

    @Test
    @DisplayName("Get order is empty track number")
    public void getOrderIsEmptyTrackNumber() {
        response = orderClient.getOrderByTrackNumber(null);
        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");

        assertThat("Code not equal", statusCode, equalTo(SC_BAD_REQUEST));
        assertThat("Message not equal", message, equalTo(MESSAGE_BAD_REQUEST));
    }

    @Test
    @DisplayName("Get order by random track number")
    public void getOrderByRandomTrackNumber() {
        response = orderClient.getOrderByTrackNumber(new Random().nextInt(Integer.MAX_VALUE));
        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");

        assertThat("Code not equal", statusCode, equalTo(SC_NOT_FOUND));
        assertThat("Message not equal", message, equalTo(MESSAGE_NOT_FOUND));
    }
}
