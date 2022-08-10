package io.rest.example;

import io.qameta.allure.Step;
import io.rest.example.helpers.HotelBookingServiceHelper;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

public class TestGet {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestGet.class);
    private HotelBookingServiceHelper hotelBookingServiceHelper;

    @BeforeClass
    public void init() {
        hotelBookingServiceHelper = new HotelBookingServiceHelper();
    }
    @Test
    @Step
    public void loginValidation() {
        ResponseOptions<?> response = hotelBookingServiceHelper.authenticateUser();
        LOGGER.info("response", response.getStatusCode());
        System.out.println("response111: "+ response.getStatusCode());
    }

    @Test(expectedExceptions = IOException.class)
    @Step
    public void bookingValidationE2E() {
        try {
            // Step-1 Create the booking
            ResponseOptions<?> createBookingResponse = hotelBookingServiceHelper.createBooking();
            LOGGER.info("response", createBookingResponse.getStatusCode());

            // Step-2 Assert that request was successful
            Assert.assertTrue(createBookingResponse.statusCode() == 200);
            Assert.assertNotNull(createBookingResponse.body().jsonPath().get("bookingid"));
            LOGGER.info("get booking response before updation",createBookingResponse.getBody().prettyPrint());
            // Step-3 With a valid Auth Token(POST), update any user's details for a new bookingid
            ResponseOptions<?> authResponse = hotelBookingServiceHelper.authenticateUser();
            LOGGER.info("Create a JSONPath object");
            JsonPath jpath = authResponse.body().jsonPath();
            authResponse.getBody();
            ResponseOptions<?> updatedBookingResponse = hotelBookingServiceHelper.updateBooking(jpath.get("bookingid"));
            Assert.assertTrue(updatedBookingResponse.statusCode() == 200);
            Assert.assertNotEquals(createBookingResponse, updatedBookingResponse);
            // Step-4 Get the updated user details using bookingid and validate (GET API)
            ResponseOptions<?> getBookingResponse = hotelBookingServiceHelper.updateBooking(jpath.get("bookingid"));
            Assert.assertTrue(getBookingResponse.statusCode() == 200);
            LOGGER.info("get booking response after updation",getBookingResponse.getBody().prettyPrint());
//            Assert.assertEquals(createBookingResponse.body().jsonPath().get("bookingid"), updatedBookingResponse.body().jsonPath().get("bookingid"));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
