package com.gestionganadera.backend.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RecaptchaResponseTest {

    @Test
    void constructorAndSetters_work() {
        RecaptchaResponse response = new RecaptchaResponse();
        response.setSuccess(true);
        response.setChallenge_ts("2025-01-01T00:00:00Z");
        response.setHostname("localhost");

        assertTrue(response.isSuccess());
        assertEquals("2025-01-01T00:00:00Z", response.getChallenge_ts());
        assertEquals("localhost", response.getHostname());
    }

    @Test
    void errorCodes_withJsonProperty_mapsCorrectly() {
        RecaptchaResponse response = new RecaptchaResponse();
        response.setSuccess(false);
        response.setErrorCodes(List.of("timeout-or-duplicate", "invalid-input-response"));

        assertFalse(response.isSuccess());
        assertEquals(2, response.getErrorCodes().size());
        assertEquals("timeout-or-duplicate", response.getErrorCodes().get(0));
    }

    @Test
    void allFields_defaults() {
        RecaptchaResponse response = new RecaptchaResponse();

        assertFalse(response.isSuccess());
        assertNull(response.getChallenge_ts());
        assertNull(response.getHostname());
        assertNull(response.getErrorCodes());
    }
}
