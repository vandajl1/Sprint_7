package ru.yandex.praktikum.order;

import io.qameta.allure.Epic;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.api.CourierClient;
import ru.yandex.praktikum.api.OrderClient;
import ru.yandex.praktikum.entity.Courier;
import ru.yandex.praktikum.entity.CourierCredentials;
import ru.yandex.praktikum.entity.Order;
import ru.yandex.praktikum.utils.GenerateCourier;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Epic("Accept order")
public class OrderAcceptTest {
    private static final String MESSAGE_BAD_REQUEST = "Недостаточно данных для поиска";
    private static final String MESSAGE_NOT_FOUND_ORDER = "Заказа с таким id не существует";
    private static final String MESSAGE_NOT_FOUND_COURIER = "Курьера с таким id не существует";
    private ValidatableResponse response;
    private OrderClient orderClient;
    private CourierClient courierClient;
    private int trackNumber;
    private int courierId;
    private int orderId;

    @Before
    public void setUp() {
        Courier courier = GenerateCourier.getRandomCourier();
        courierClient = new CourierClient();
        courierClient.createCourier(courier);
        response = courierClient.loginCourier(new CourierCredentials(courier.getLogin(), courier.getPassword()));
        courierId = response.extract().path("id");

        orderClient = new OrderClient();
        response = orderClient.createOrder(Order.getFinalInstanceOrder());
        trackNumber = response.extract().path("track");
    }

    @After
    public void clearState() {
        courierClient.deleteCourier(courierId);
    }

    @Test
    @DisplayName("Accept order is valid credentials")
    public void acceptOrderValidCredentials() {
        response = orderClient.getOrderByTrackNumber(trackNumber);
        orderId = response.extract().path("order.id");
        response = orderClient.acceptOrder(orderId, courierId);
        boolean isAccept = response.extract().path("ok");

        assertThat("Accept order incorrect", isAccept, equalTo(true));
    }

    @Test
    @DisplayName("Accept order is empty order id")
    public void acceptOrderIsEmptyOrderId() {
        orderClient.getOrderByTrackNumber(trackNumber);
        response = orderClient.acceptOrder(null, courierId);
        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");

        assertThat("Code not equal", statusCode, equalTo(SC_BAD_REQUEST));
        assertThat("Message not equal", message, equalTo(MESSAGE_BAD_REQUEST));
    }

    @Test
    @DisplayName("Accept order is empty courier id")
    public void acceptOrderIsEmptyCourierId() {
        orderClient.getOrderByTrackNumber(trackNumber);
        response = orderClient.acceptOrder(orderId, null);
        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");

        assertThat("Code not equal", statusCode, equalTo(SC_BAD_REQUEST));
        assertThat("Message not equal", message, equalTo(MESSAGE_BAD_REQUEST));
    }

    @Test
    @DisplayName("Accept order by zero order id")
    public void acceptOrderByZeroOrderId() {
        orderClient.getOrderByTrackNumber(trackNumber);
        response = orderClient.acceptOrder(0, courierId);
        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");

        assertThat("Code not equal", statusCode, equalTo(SC_NOT_FOUND));
        assertThat("Message not equal", message, equalTo(MESSAGE_NOT_FOUND_ORDER));
    }

    @Test
    @DisplayName("Accept order by zero courier id")
    public void acceptOrderByZeroCourierId() {
        orderClient.getOrderByTrackNumber(trackNumber);
        response = orderClient.acceptOrder(orderId, 0);
        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");

        assertThat("Code not equal", statusCode, equalTo(SC_NOT_FOUND));
        assertThat("Message not equal", message, equalTo(MESSAGE_NOT_FOUND_COURIER));
    }
}
