package ru.yandex.praktikum.api;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import ru.yandex.praktikum.config.ScooterConfig;
import ru.yandex.praktikum.entity.Order;
import ru.yandex.praktikum.utils.EndPoints;
import static io.restassured.RestAssured.given;

public class OrderClient extends ScooterConfig {
    @Step("Send POST request to /api/v1/orders")
    public ValidatableResponse createOrder(Order order) {
        return given()
                .spec(getBaseSpec())
                .body(order)
                .log().all()
                .post(EndPoints.ORDER_PATH)
                .then()
                .log().all();
    }

    @Step("Send PUT request to /api/v1/orders/finish/{orderId}")
    public ValidatableResponse finishOrderByOrderId(Integer orderId) {
        return given()
                .spec(getBaseSpec())
                .log().all()
                .put(EndPoints.ORDER_PATH + "finish/" + orderId)
                .then()
                .log().all();
    }

    @Step("Send PUT request to /api/v1/orders/finish/")
    public ValidatableResponse finishOrderIsEmptyOrderId() {
        return given()
                .spec(getBaseSpec())
                .log().all()
                .put(EndPoints.ORDER_PATH + "finish/")
                .then()
                .log().all();
    }

    @Step("Send PUT request to /api/v1/orders/cancel/{trackNumber}")
    public ValidatableResponse cancelOrder(Integer trackNumber) {
        return given()
                .spec(getBaseSpec())
                .queryParam("track", trackNumber)
                .log().all()
                .put(EndPoints.ORDER_PATH + "cancel/")
                .then()
                .log().all();
    }

    @Step("Send GET request to /api/v1/orders/track?t={trackNumber}")
    public ValidatableResponse getOrderByTrackNumber(Integer trackNumber) {
        return given()
                .spec(getBaseSpec())
                .queryParam("t", trackNumber)
                .log().all()
                .get(EndPoints.ORDER_PATH + "track")
                .then()
                .log().all();
    }

    @Step("Send GET request to /api/v1/orders?{courierId}")
    public ValidatableResponse getAllOrders(Integer courierId) {
        return given()
                .spec(getBaseSpec())
                .queryParam("courierId", courierId)
                .log().all()
                .get(EndPoints.ORDER_PATH)
                .then()
                .log().all();
    }

    @Step("Send GET request to /api/v1/orders?{courierId}{nearestStation}")
    public ValidatableResponse getAllOrdersNearestStation(Integer courierId, String[] nearestStation) {
        return given()
                .spec(getBaseSpec())
                .queryParam("courierId", courierId)
                .queryParams("nearestStation", nearestStation)
                .log().all()
                .get(EndPoints.ORDER_PATH)
                .then()
                .log().all();
    }

    @Step("Send PUT request to /api/v1/orders/accept/{orderId}?courierId={courierId}")
    private ValidatableResponse acceptByOrderIdAndCourierId(Integer orderId, Integer courierId) {
        return given()
                .spec(getBaseSpec())
                .queryParam("courierId", courierId)
                .log().all()
                .put(EndPoints.ORDER_PATH + "accept/" + orderId)
                .then()
                .log().all();
    }

    @Step("Send PUT request to /api/v1/orders/accept/?courierId={courierId}")
    private ValidatableResponse acceptByCourierId(Integer courierId) {
         return given()
                .spec(getBaseSpec())
                .log().all()
                .put(EndPoints.ORDER_PATH + "accept/" + courierId)
                .then()
                .log().all();
    }

    public ValidatableResponse acceptOrder(Integer orderId, Integer courierId) {
        return orderId != null ? acceptByOrderIdAndCourierId(orderId, courierId) : acceptByCourierId(courierId);
    }

    public ValidatableResponse finishOrder(Integer orderId) {
        return orderId != null ? finishOrderByOrderId(orderId) : finishOrderIsEmptyOrderId();
    }
}
