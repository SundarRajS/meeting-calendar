package com.meeting.booking.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import java.util.ArrayList;
import java.util.List;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.meeting.booking.model.Meeting;
import com.meeting.booking.utils.FileUtils;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class MeetingServiceTest {

	private MeetingService meetingService;

	private MockedStatic<FileUtils> fileUtils;

	private DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("HH:mm");

	private MockMultipartHttpServletRequestBuilder builder;

	private MockMultipartFile file;

	@BeforeAll
	public void setupService() {
		this.meetingService = new MeetingServiceImpl();
		this.builder = MockMvcRequestBuilders.multipart("/meeting-calendar");
	}

	private MockMultipartFile createFile() {
		MockMultipartFile file = new MockMultipartFile("file", "meeting.txt", MediaType.TEXT_PLAIN_VALUE,
				new String("0900 1730\n" + "2020-01-18 10:17:06 EMP001\n" + "2020-01-21 09:00 2\n"
						+ "2020-01-18 12:34:56 EMP002\n" + "2020-01-21 09:00 2").getBytes());
		return file;
	}

	@BeforeEach
	public void setUpTests() {
		this.fileUtils = Mockito.mockStatic(FileUtils.class);
	}

	@AfterEach
	public void cleanUp() {
		this.fileUtils.close();
	}

	@Test
	@DisplayName("Test the meeting request processing - valid scenario")
	public void testMeetingRequestProcessing() {
		List<String> content = new ArrayList<>();
		content.add("0900 1730");
		content.add("2020-01-18 10:17:06 EMP001");
		content.add("2020-01-21 09:00 2");
		fileUtils.when(() -> FileUtils.readInputFile(file)).thenReturn(content);
		List<Meeting> meetings = this.meetingService.processMeetingRequests(file);
		assertEquals(meetings.get(0).getBookings().get(0).getEmp_id(), "EMP001");
	}

	@Test
	@DisplayName("Test the meeting request processing - Out of office hours")
	public void testMeetingRequestProcessingOutofHours() {
		List<String> content = new ArrayList<>();
		content.add("0900 1730");
		content.add("2020-01-18 10:17:06 EMP001");
		content.add("2020-01-21 17:00 2");
		fileUtils.when(() -> FileUtils.readInputFile(file)).thenReturn(content);
		List<Meeting> meetings = this.meetingService.processMeetingRequests(file);
		assertEquals(meetings.size(), 0);
	}

	@Test
	@DisplayName("Test the meeting request processing - One of the request is overlapping with the previous one")
	public void testOverlappingRequests() {
		List<String> content = new ArrayList<>();
		content.add("0900 1730");
		content.add("2020-01-18 10:17:06 EMP001");
		content.add("2020-01-21 09:00 2");
		content.add("2020-01-18 10:17:06 EMP002");
		content.add("2020-01-21 10:00 2");
		fileUtils.when(() -> FileUtils.readInputFile(file)).thenReturn(content);
		List<Meeting> meetings = this.meetingService.processMeetingRequests(file);
		assertEquals(meetings.size(), 1);
		assertEquals(meetings.get(0).getBookings().get(0).getEmp_id(), "EMP001");
		assertEquals(timeFormatter.parseLocalTime("09:00"), meetings.get(0).getBookings().get(0).getStart_time());
		assertEquals(timeFormatter.parseLocalTime("11:00"), meetings.get(0).getBookings().get(0).getEnd_time());
	}

	@Test
	@DisplayName("Test the meeting request processing - One of the request is overlapping - 1st request is booked")
	public void testOverlappingRequestsFirstSubmissionBooked() {
		List<String> content = new ArrayList<>();
		content.add("0900 1730");
		content.add("2020-01-18 10:17:06 EMP002");
		content.add("2020-01-21 10:00 2");
		content.add("2020-01-18 10:17:06 EMP001");
		content.add("2020-01-21 09:00 2");
		fileUtils.when(() -> FileUtils.readInputFile(file)).thenReturn(content);
		List<Meeting> meetings = this.meetingService.processMeetingRequests(file);
		assertEquals(meetings.size(), 1);
		assertEquals(meetings.get(0).getBookings().get(0).getEmp_id(), "EMP002");
		assertEquals(timeFormatter.parseLocalTime("10:00"), meetings.get(0).getBookings().get(0).getStart_time());
		assertEquals(timeFormatter.parseLocalTime("12:00"), meetings.get(0).getBookings().get(0).getEnd_time());
	}

	@Test
	@DisplayName("Test the meeting request processing - Incorrect office hours passed")
	public void testIncorrectOfficeHoursInTextFile() {
		List<String> content = new ArrayList<>();
		content.add("0900 2530");
		content.add("2020-01-18 10:17:06 EMP001");
		content.add("2020-01-21 09:00 2");
		fileUtils.when(() -> FileUtils.readInputFile(file)).thenReturn(content);
		List<Meeting> meetings = this.meetingService.processMeetingRequests(file);
		assertNull(meetings);
	}

	@Test
	@DisplayName("Test the meeting request processing - Incorrect meeting date passed")
	public void testIncorrectMeeting() {
		List<String> content = new ArrayList<>();
		content.add("0900 1730");
		content.add("2020-01-18 10:17:06 EMP001");
		content.add("2020-01-32 09:00 2");
		fileUtils.when(() -> FileUtils.readInputFile(file)).thenReturn(content);
		List<Meeting> meetings = this.meetingService.processMeetingRequests(file);
		assertNull(meetings);
	}

	@Test
	@DisplayName("Test the meeting request processing - Incorrect meeting hours passed")
	public void testIncorrectMeetingHours() {
		List<String> content = new ArrayList<>();
		content.add("0900 1730");
		content.add("2020-01-18 10:17:06 EMP001");
		content.add("2020-01-20 25:00 2");
		fileUtils.when(() -> FileUtils.readInputFile(file)).thenReturn(content);
		List<Meeting> meetings = this.meetingService.processMeetingRequests(file);
		assertNull(meetings);
	}

	@Test
	@DisplayName("Test the meeting request processing order")
	public void testChronologicalOrder() {
		List<String> content = new ArrayList<>();
		content.add("0900 1730");
		content.add("2020-01-18 10:17:06 EMP001");
		content.add("2020-01-20 09:00 2");
		content.add("2020-01-18 10:17:06 EMP002");
		content.add("2020-01-20 11:00 2");
		fileUtils.when(() -> FileUtils.readInputFile(file)).thenReturn(content);
		List<Meeting> meetings = this.meetingService.processMeetingRequests(file);
		assertEquals(meetings.get(0).getBookings().get(0).getEmp_id(), "EMP001");
		assertEquals(timeFormatter.parseLocalTime("09:00"), meetings.get(0).getBookings().get(0).getStart_time());
		assertEquals(timeFormatter.parseLocalTime("11:00"), meetings.get(0).getBookings().get(0).getEnd_time());
		assertEquals(meetings.get(0).getBookings().get(1).getEmp_id(), "EMP002");
		assertEquals(timeFormatter.parseLocalTime("11:00"), meetings.get(0).getBookings().get(1).getStart_time());
		assertEquals(timeFormatter.parseLocalTime("13:00"), meetings.get(0).getBookings().get(1).getEnd_time());
	}
}
