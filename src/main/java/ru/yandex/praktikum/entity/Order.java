package ru.yandex.praktikum.entity;

import lombok.Builder;
import lombok.Data;
import java.util.Arrays;
import io.qameta.allure.Allure;

@Data
@Builder
public class Order {
    private String firstName;
    private String lastName;
    private String address;
    private int metroStation;
    private String phone;
    private int rentTime;
    private String deliveryDate;
    private String comment;
    private String[] color;

    public void addAttachmentOrder() {
        Allure.addAttachment("First name : ", firstName);
        Allure.addAttachment("Last name : ", lastName);
        Allure.addAttachment("Address : ", address);
        Allure.addAttachment("Metro station: ", String.valueOf(metroStation));
        Allure.addAttachment("Phone : ", phone);
        Allure.addAttachment("Rent time : ", String.valueOf(rentTime));
        Allure.addAttachment("Delivery date : ", deliveryDate);
        Allure.addAttachment("Comment : ", comment);
        Allure.addAttachment("Color : ", Arrays.toString(color));
    }

    public static Order getFinalInstanceOrder() {
        final Order order = Order.builder()
                .firstName("Naruto")
                .lastName("Uchiha")
                .address("Konoha, 142 apt.")
                .metroStation(1)
                .phone("+7 800 355 35 35")
                .rentTime(5)
                .deliveryDate("2020-06-06")
                .comment("Saske, come back to Konoha")
                .color(new String[]{"BLACK"})
                .build();
        order.addAttachmentOrder();
        return order;
    }
}
