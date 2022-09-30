package com.meeting.booking.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.meeting.booking.model.Booking;
import com.meeting.booking.model.Meeting;
import com.meeting.booking.utils.BookingUtils;
import com.meeting.booking.utils.FileUtils;

@Service
public class MeetingServiceImpl implements MeetingService {

	private static Logger logger = LoggerFactory.getLogger(MeetingServiceImpl.class);

	private DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");

	private DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("HH:mm");

	@Override
	public List<Meeting> processMeetingRequests(MultipartFile file) {
		// TODO Auto-generated method stub
		List<String> content = FileUtils.readInputFile(file);
		Map<LocalDate, Set<Booking>> dataset = null;
		List<Meeting> meetings = Collections.emptyList();
		if (content != null && content.size() > 0) {
			try {
				String officeTime = content.get(0);
				int officeStartHours = Integer.parseInt(officeTime.split(" ")[0].substring(0, 2));
				int officeStartMinutes = Integer.parseInt(officeTime.split(" ")[0].substring(2, 4));
				LocalTime officeStartTime = new LocalTime(officeStartHours, officeStartMinutes);
				int officeEndHours = Integer.parseInt(officeTime.split(" ")[1].substring(0, 2));
				int officeEndMinutes = Integer.parseInt(officeTime.split(" ")[1].substring(2, 4));
				LocalTime officeEndTime = new LocalTime(officeEndHours, officeEndMinutes);

				dataset = new TreeMap<LocalDate, Set<Booking>>();

				for (int i = 1; i < content.size(); i = i + 2) {
					List<String> bookingRequest = Arrays.asList(content.get(i + 1).split(" "));
					/* get the meeting date which is 1st element in the given array */
					LocalDate meetingDate = dateFormatter.parseLocalDate(bookingRequest.get(0));

					Booking booking = getMeetingInformation(content.get(i), officeStartTime, officeEndTime,
							bookingRequest);
					if (booking != null) {
						if (dataset.containsKey(meetingDate)) {
							Set<Booking> bookings = dataset.get(meetingDate);
							boolean isOverlapping = isThereOverlapingRanges(bookings, booking);
							if (!bookings.contains(booking) && !isOverlapping) {
								bookings.add(booking);
							}
						} else {
							Set<Booking> meetingsForDay = new TreeSet<Booking>();
							meetingsForDay.add(booking);
							dataset.put(meetingDate, meetingsForDay);
						}
					}
				}
				meetings = this.generateMeetingCalendar(dataset);
			} catch (Exception e) {
				logger.error("Incorrect data in the file:" + e.getMessage());
				return null;
			}

		}
		return meetings;
	}

	private List<Meeting> generateMeetingCalendar(Map<LocalDate, Set<Booking>> dataset) {
		List<Meeting> meetings = new ArrayList<>();
		if (dataset != null) {
			Iterator<Entry<LocalDate, Set<Booking>>> iterator = dataset.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<LocalDate, Set<Booking>> entry = iterator.next();
				Meeting meeting = new Meeting();
				meeting.setDate(entry.getKey());
				if (entry.getValue() != null && entry.getValue().size() > 0) {
					meeting.setBookings(new ArrayList<>(entry.getValue()));
				}
				meetings.add(meeting);
			}
		}
		return meetings;
	}

	private boolean isThereOverlapingRanges(Set<Booking> bookings, Booking toBeAdded) {
		if (bookings.size() == 0) {
			return false;
		}
		List<Booking> bookingList = new ArrayList<>(bookings);
		boolean isOverlappingTime = false;
		for (int i = 0; i < bookings.size(); i++) {
			isOverlappingTime = BookingUtils.isOverlappingTime(bookingList.get(i), toBeAdded);
			if (isOverlappingTime) {
				break;
			}
		}
		return isOverlappingTime;
	}

	private Booking getMeetingInformation(String inputData, LocalTime officeStartTime, LocalTime officeEndTime,
			List<String> bookingRequest) {
		/* get the employee id from the input */
		String employeeId = inputData.split(" ")[2];
		/* get the start time from the request file */
		LocalTime meetingStartTime = timeFormatter.parseLocalTime(bookingRequest.get(1));
		/* calculate the end time using start time and duration of the meeting */
		LocalTime meetingEndTime = new LocalTime(meetingStartTime.getHourOfDay(), meetingStartTime.getMinuteOfHour())
				.plusHours(Integer.parseInt(bookingRequest.get(2)));

		boolean isValidMeetingRequest = BookingUtils.isValidMeetingTime(officeStartTime, officeEndTime,
				meetingStartTime, meetingEndTime);

		if (isValidMeetingRequest) {
			return new Booking(employeeId, meetingStartTime, meetingEndTime);
		} else {
			logger.error(
					"Employee " + employeeId + " tried booking the meeting in the out office hours" + bookingRequest);
			return null;
		}
	}

}
