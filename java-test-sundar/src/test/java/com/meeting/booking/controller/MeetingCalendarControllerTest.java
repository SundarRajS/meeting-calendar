package com.meeting.booking.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matchers;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.meeting.booking.model.Booking;
import com.meeting.booking.model.Meeting;
import com.meeting.booking.service.MeetingService;

@WebMvcTest(MeetingCalendarController.class)
@TestInstance(Lifecycle.PER_CLASS)
public class MeetingCalendarControllerTest {

	@MockBean
	private MeetingService meetingService;

	@Autowired
	MockMvc mockMvc;

	private DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("HH:mm");
	
	private MockMultipartHttpServletRequestBuilder builder;
	
	private MockMultipartFile file;
	
	@BeforeAll
	public void initSetup() {
		this.file = createFile();
		this.builder = MockMvcRequestBuilders.multipart("/meeting-calendar");
	}
	
	private MockMultipartFile createFile() {
		MockMultipartFile file 
	      = new MockMultipartFile(
	        "file", 
	        "meeting.txt", 
	        MediaType.TEXT_PLAIN_VALUE, 
	        new String("0900 1730\n"
	        		+ "2020-01-18 10:17:06 EMP001\n"
	        		+ "2020-01-21 09:00 2\n"
	        		+ "2020-01-18 12:34:56 EMP002\n"
	        		+ "2020-01-21 09:00 2").getBytes()
	      );
		return file;
	}

	@Test
	@DisplayName("check the meeting response")
	public void processMeetingRequests() throws Exception {
		List<Meeting> meetings = new ArrayList<>();
		List<Booking> bookings = new ArrayList<>();
		bookings.add(
				new Booking("EMP001", timeFormatter.parseLocalTime("09:00"), timeFormatter.parseLocalTime("11:00")));
		bookings.add(
				new Booking("EMP002", timeFormatter.parseLocalTime("11:00"), timeFormatter.parseLocalTime("13:00")));
		meetings.add(new Meeting(new LocalDate("2020-01-20"), bookings));
		Mockito.when(meetingService.processMeetingRequests(file)).thenReturn(meetings);
		mockMvc.perform(builder.file(file)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().json(
						"[{\"date\":\"2020-01-20\",\"bookings\":[{\"emp_id\":\"EMP001\",\"start_time\":\"09:00\",\"end_time\":\"11:00\"},"
								+ "{\"emp_id\":\"EMP002\",\"start_time\":\"11:00\",\"end_time\":\"01:00\"}]}]"));

	}

	@Test
	@DisplayName("Check if the meeting displayed in order")
	public void processMeetingRequestsCheckOrder() throws Exception {
		List<Meeting> meetings = new ArrayList<>();
		List<Booking> bookings1 = new ArrayList<>();
		bookings1.add(
				new Booking("EMP001", timeFormatter.parseLocalTime("09:00"), timeFormatter.parseLocalTime("11:00")));
		List<Booking> bookings2 = new ArrayList<>();
		bookings2.add(
				new Booking("EMP002", timeFormatter.parseLocalTime("11:00"), timeFormatter.parseLocalTime("13:00")));
		meetings.add(new Meeting(new LocalDate("2020-01-20"), bookings1));
		meetings.add(new Meeting(new LocalDate("2020-01-21"), bookings2));
		Mockito.when(meetingService.processMeetingRequests(file)).thenReturn(meetings);
		mockMvc.perform(builder.file(file)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].date", Matchers.is("2020-01-20")))
				.andExpect(jsonPath("$[0].bookings[0].emp_id", Matchers.is("EMP001")))
				.andExpect(jsonPath("$[1].date", Matchers.is("2020-01-21")))
				.andExpect(jsonPath("$[1].bookings[0].emp_id", Matchers.is("EMP002")));
	}

	@Test
	@DisplayName("Check if the meeting service returns value as null when there is no flat file")
	public void processMeetingRequestsMissingTextFile() throws Exception {
		Mockito.when(meetingService.processMeetingRequests(file)).thenReturn(null);
		MvcResult mvcResult = mockMvc.perform(builder.file(file)).andExpect(status().isOk()).andReturn();
		assertEquals(mvcResult.getResponse().getContentLength(), 0);
	}

}
