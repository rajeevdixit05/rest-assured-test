package io.rest.example.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Step;
import io.rest.example.constants.Endpoints;
import io.rest.example.model.Booking;
import io.rest.example.model.Bookingdates;
import io.rest.example.utils.ConfigManager;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ResponseOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class HotelBookingServiceHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(HotelBookingServiceHelper.class);
    private static final String BASE_URL = ConfigManager.getInstance().getString("api.base_url");

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private Integer id;
    private static String valid_userName = "admin";
    private static String valid_password = "password123";
    public HotelBookingServiceHelper() {
        RestAssured.useRelaxedHTTPSValidation();
        RestAssured.baseURI = BASE_URL;
        RestAssured.filters(new io.qameta.allure.restassured.AllureRestAssured());
    }
    @Step
    public ResponseOptions<?> authenticateUser() {

        ResponseOptions<?> response = RestAssured
                .given()
                .auth().basic(valid_userName, valid_password)
                .when()
                .post(Endpoints.USER_AUTH_ENDPOINT)
                .thenReturn();

        LOGGER.info("response => {}", response);
        System.out.println("response from helper => {}"+ response.getStatusCode());
        return response;
    }

    @Step
    public ResponseOptions<?> createBooking() throws IOException {
        // creating `Booking` object
        Bookingdates bookingdates = new Bookingdates("2018-01-01", "2019-01-01");
        Booking booking = new Booking("Jim", "Brown", 111, true, bookingdates, "Breakfast");

        // converting `Booking` object to JSON string using `ObjectMapper`
        byte[] data = MAPPER.writeValueAsBytes(booking);
        String json = MAPPER.writeValueAsString(booking);

        LOGGER.info("serialization of `Booking` class into JSON string using `ObjectMapper` => {}", new String(data));
        LOGGER.info("serialization of `Booking` class into JSON string using `ObjectMapper` => {}", json);

        ResponseOptions<?> response = RestAssured
                            .given()
                            .header("content-type", "application/json")
                            .body(booking.toString())
                            .post(Endpoints.CREATE_BOOKING_ENDPOINT)
                            .thenReturn();

        LOGGER.info("response => {}", response);
        System.out.println("response from helper => {}"+ response.getStatusCode());
        return response;
    }

    @Step
    public ResponseOptions<?> updateBooking(int bookingid) {
        Bookingdates bookingdates1 = new Bookingdates("2018-01-01", "2019-01-01");
        Booking booking1 = new Booking("Jim2", "Brown2", 222, true, bookingdates1, "Lunch");
        ResponseOptions<?> response = RestAssured
                .given()
                .auth().basic(valid_userName, valid_password)
                .when()
                .body(booking1.toString())
                .put(Endpoints.USER_AUTH_ENDPOINT+"/"+bookingid)
                .thenReturn();

        LOGGER.info("response => {}", response);
        System.out.println("response from helper => {}"+ response.getStatusCode());
        return response;
    }

    @Step
    public ResponseOptions<?> getBooking(int bookingid) {

        ResponseOptions<?> response = RestAssured
                .given()
                .auth().basic(valid_userName, valid_password)
                .when()
                .get(Endpoints.USER_AUTH_ENDPOINT+"/"+bookingid)
                .thenReturn();

        LOGGER.info("response => {}", response);
        System.out.println("response from helper => {}"+ response.getStatusCode());
        return response;
    }
}
