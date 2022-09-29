package com.meeting.booking.utils;

import org.joda.time.Interval;
import org.joda.time.LocalTime;

import com.meeting.booking.model.Booking;

public interface BookingUtils {

	public static boolean isOverlappingTime(Booking one, Booking two) {
		if (one == null || two == null) {
			return false;
		}
		Interval meetingInterval = new Interval(one.getStart_time().toDateTimeToday(),
				one.getEnd_time().toDateTimeToday());
		Interval toCompareMeetingInterval = new Interval(two.getStart_time().toDateTimeToday(),
				two.getEnd_time().toDateTimeToday());
		return meetingInterval.overlaps(toCompareMeetingInterval);
	}

	public static boolean isValidMeetingTime(LocalTime officeStartTime, LocalTime officeEndTime,
			LocalTime meetingStartTime, LocalTime meetingEndTime) {
		/* check if the meeting hours falls within the office hours */
		return (meetingStartTime.isEqual(officeStartTime) || meetingStartTime.isAfter(officeStartTime))
				&& (meetingEndTime.isBefore(officeEndTime) || meetingEndTime.isEqual(officeEndTime));
	}
}
