package com.meeting.booking.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.meeting.booking.model.Meeting;

public interface MeetingService {

	public List<Meeting> processMeetingRequests(MultipartFile file);
}
