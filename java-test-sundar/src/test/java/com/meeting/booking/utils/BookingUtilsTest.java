package com.meeting.booking.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.meeting.booking.model.Booking;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class BookingUtilsTest {

	private DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("HH:mm");

	@Test
	@DisplayName("Test to identify if the meeting time in the request overlap with each other - Overlapping time")
	public void testMeetingTimeOverlap1() {
		Booking booking1 = new Booking("EMP001", timeFormatter.parseLocalTime("09:00"),
				timeFormatter.parseLocalTime("11:00"));
		Booking booking2 = new Booking("EMP002", timeFormatter.parseLocalTime("10:00"),
				timeFormatter.parseLocalTime("11:00"));
		assertTrue(BookingUtils.isOverlappingTime(booking1, booking2));
	}

	@Test
	@DisplayName("Test to identify if the meeting time in the request overlap with each other when one of the Booking is null")
	public void testMeetingTimeOverlapIfOneIsNull() {
		Booking booking1 = new Booking("EMP002", timeFormatter.parseLocalTime("09:00"),
				timeFormatter.parseLocalTime("11:00"));
		Booking booking2 = null;
		assertFalse(BookingUtils.isOverlappingTime(booking1, booking2));
	}

	@Test
	@DisplayName("Test to identify if the meeting time in the request overlap with each other - Non-Overlapping time")
	public void testMeetingTimeOverlap2() {
		Booking booking1 = new Booking("EMP002", timeFormatter.parseLocalTime("09:00"),
				timeFormatter.parseLocalTime("11:00"));
		Booking booking2 = new Booking("EMP002", timeFormatter.parseLocalTime("11:00"),
				timeFormatter.parseLocalTime("13:00"));
		assertFalse(BookingUtils.isOverlappingTime(booking1, booking2));
	}

	@Test
	@DisplayName("Test to indentify if the meeting request falls on office hours - invalid")
	public void testMeetingTimeInValidOfficeHours() {
		LocalTime officeStartTime = timeFormatter.parseLocalTime("09:00");
		LocalTime officeEndTime = timeFormatter.parseLocalTime("17:30");
		LocalTime startTime = timeFormatter.parseLocalTime("17:00");
		LocalTime endTime = timeFormatter.parseLocalTime("19:00");
		assertFalse(BookingUtils.isValidMeetingTime(startTime, endTime, officeStartTime, officeEndTime));
	}

	@Test
	@DisplayName("Test to indentify if the meeting request falls on office hours - valid")
	public void testMeetingTimeValidOfficeHours() {
		LocalTime officeStartTime = timeFormatter.parseLocalTime("09:00");
		LocalTime officeEndTime = timeFormatter.parseLocalTime("17:30");
		LocalTime startTime = timeFormatter.parseLocalTime("14:00");
		LocalTime endTime = timeFormatter.parseLocalTime("16:00");
		assertTrue(BookingUtils.isValidMeetingTime(officeStartTime, officeEndTime, startTime, endTime));
	}
}
