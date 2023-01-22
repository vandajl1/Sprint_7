package ru.yandex.praktikum.order;

import java.util.Random;
import io.qameta.allure.Epic;
import io.qameta.allure.Issue;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.api.OrderClient;
import ru.yandex.praktikum.entity.Order;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Epic("Cancel order")
public class OrderCancelTest {
    private static final String MESSAGE_BAD_REQUEST = "Недостаточно данных для поиска";
    private static final String MESSAGE_NOT_FOUND = "Заказ не найден";
        private static final String MESSAGE_CONFLICT = "Заказ нельзя завершить";
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
    @DisplayName("Cancel order is valid credentials")
    public void cancelOrderValidCredentials() {
        response = orderClient.cancelOrder(trackNumber);
        int statusCode = response.extract().statusCode();
        boolean isCancel = response.extract().path("ok");

        assertThat("Code not equal", statusCode, equalTo(SC_OK));
        assertThat("Message not equal", isCancel, equalTo(true));
    }

    @Test
    @DisplayName("Cancel order is empty track number")
    public void cancelOrderIsEmptyTrackNumber() {
        response = orderClient.cancelOrder(null);
        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");

        assertThat("Code not equal", statusCode, equalTo(SC_BAD_REQUEST));
        assertThat("Message not equal", message, equalTo(MESSAGE_BAD_REQUEST));
    }

    @Test
    @DisplayName("Cancel order by random track number")
    public void cancelOrderByRandomTrackNumber() {
        response = orderClient.cancelOrder(new Random().nextInt());
        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");

        assertThat("Code not equal", statusCode, equalTo(SC_NOT_FOUND));
        assertThat("Message not equal", message, equalTo(MESSAGE_NOT_FOUND));
    }

    @Test
    @DisplayName("Repeated request by track number")
    @Issue("BUG-Message not equal")
    public void cancelOrderRepeatedRequestByTrackNumber() {
        orderClient.cancelOrder(trackNumber);
        response = orderClient.cancelOrder(trackNumber);
        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");

        assertThat("Code not equal", statusCode, equalTo(SC_CONFLICT));
        assertThat("Message not equal", message, equalTo(MESSAGE_CONFLICT));
    }
}
