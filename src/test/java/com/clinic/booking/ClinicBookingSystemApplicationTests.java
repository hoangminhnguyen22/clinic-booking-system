package com.clinic.booking;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Clock;
import java.time.ZoneId;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ClinicBookingSystemApplicationTests {
	@Autowired
	private Clock clock;

	@Test
	void contextLoads() {
	}

	@Test
	void shouldConfigureClockForClinicTimezone() {
		assertEquals(
				ZoneId.of("Asia/Ho_Chi_Minh"),
				clock.getZone());
	}

}
