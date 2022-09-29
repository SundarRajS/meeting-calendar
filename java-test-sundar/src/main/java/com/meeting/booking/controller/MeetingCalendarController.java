package com.meeting.booking.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.meeting.booking.model.Meeting;
import com.meeting.booking.service.MeetingService;

@RestController
@RequestMapping(path = "/")
public class MeetingCalendarController {

	@Autowired
	private MeetingService meetingService;

	@PostMapping(path = "/meeting-calendar", produces = "application/json")
	public ResponseEntity<?> getMeetingCalendar(@RequestParam("file") MultipartFile file) {
		List<Meeting> meetings = this.meetingService.processMeetingRequests(file);
		return new ResponseEntity<List<Meeting>>(meetings, HttpStatus.OK);
	}

}
