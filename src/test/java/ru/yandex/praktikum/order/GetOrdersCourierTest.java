package ru.yandex.praktikum.order;

import io.qameta.allure.Epic;
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
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Epic("Get orders courier")
public class GetOrdersCourierTest {
    private static final String MESSAGE_NOT_FOUND = "Курьер с идентификатором 0 не найден";
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
    @DisplayName("Get all orders courier")
    public void getAllOrders() {
        response = orderClient.getOrderByTrackNumber(trackNumber);
        int orderId = response.extract().path("order.id");
        orderClient.acceptOrder(orderId, courierId);
        response = orderClient.getAllOrders(courierId);
        int statusCode = response.extract().statusCode();

        assertThat("Code not equal", statusCode, equalTo(SC_OK));
    }

    @Test
    @DisplayName("Get all orders courier nearest station metro")
    public void getAllOrdersNearestStation() {
        response = orderClient.getAllOrdersNearestStation(courierId, new String[] {"1", "2"});
        int statusCode = response.extract().statusCode();

        assertThat("Code not equal", statusCode, equalTo(SC_OK));
    }

    @Test
    @DisplayName("Get all orders courier nearest station by zero courier id")
    public void getAllOrdersNearestStationByZeroCourierId() {
        response = orderClient.getAllOrdersNearestStation(0, new String[] {"1", "2"});
        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");

        assertThat("Code not equal", statusCode, equalTo(SC_NOT_FOUND));
        assertThat("Message not equal", message, equalTo(MESSAGE_NOT_FOUND));
    }

    @Test
    @DisplayName("Get all orders courier nearest station is empty courier id")
    public void getAllOrdersNearestStationIsEmptyCourierId() {
        response = orderClient.getAllOrdersNearestStation(null, new String[] {"1", "2"});
        int statusCode = response.extract().statusCode();

        assertThat("Code not equal", statusCode, equalTo(SC_OK));
    }
}
