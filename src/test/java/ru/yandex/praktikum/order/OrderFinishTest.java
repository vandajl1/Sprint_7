package ru.yandex.praktikum.order;

import java.util.Random;
import io.qameta.allure.Epic;
import io.qameta.allure.Issue;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.api.CourierClient;
import ru.yandex.praktikum.api.OrderClient;
import ru.yandex.praktikum.entity.Courier;
import ru.yandex.praktikum.entity.CourierCredentials;
import ru.yandex.praktikum.entity.Order;
import ru.yandex.praktikum.utils.GenerateCourier;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Epic("Finish order")
public class OrderFinishTest {
    private static final String MESSAGE_NOT_FOUND_COURIER = "Курьера с таким id не существует";
    private static final String MESSAGE_NOT_FOUND_ORDER = "Заказа с таким id не существует";
    private ValidatableResponse response;
    private OrderClient orderClient;
    private CourierClient courierClient;
    private int trackNumber;
    private int courierId;

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

    @Test
    @DisplayName("Finish order is valid order id")
    public void finishOrderValidOrderId() {
        response = orderClient.getOrderByTrackNumber(trackNumber);
        int orderId = response.extract().path("order.id");
        orderClient.acceptOrder(orderId, courierId);
        response = orderClient.finishOrder(orderId);
        int statusCode = response.extract().statusCode();
        boolean isFinish = response.extract().path("ok");

        assertThat("Code not equal", statusCode, equalTo(SC_OK));
        assertThat("Order finish incorrect", isFinish, equalTo(true));
    }

    @Test
    @DisplayName("Finish order is empty order id")
    @Issue("BUG-Code not equal")
    public void finishOrderIsEmptyOrderId() {
        response = orderClient.finishOrder(null);
        int statusCode = response.extract().statusCode();

        assertThat("Code not equal", statusCode, equalTo(SC_NOT_FOUND));
    }

    @Test
    @DisplayName("Finish order by random order id")
    public void finishOrderByRandomOrderId() {
        response = orderClient.finishOrder(new Random().nextInt());
        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");

        assertThat("Code not equal", statusCode, equalTo(SC_NOT_FOUND));
        assertThat("Message not equal", message, equalTo(MESSAGE_NOT_FOUND_ORDER));
    }

    @Test
    @DisplayName("Finish order by random courier id")
    public void finishOrderByRandomCourierId() {
        response = orderClient.getOrderByTrackNumber(trackNumber);
        int orderId = response.extract().path("order.id");
        response = orderClient.acceptOrder(orderId, new Random().nextInt());
        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");

        assertThat("Code not equal", statusCode, equalTo(SC_NOT_FOUND));
        assertThat("Message not equal", message, equalTo(MESSAGE_NOT_FOUND_COURIER));
    }
}
