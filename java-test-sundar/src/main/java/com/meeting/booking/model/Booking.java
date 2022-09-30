package com.meeting.booking.model;

import org.joda.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.meeting.booking.utils.BookingUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonFormat(pattern="HH:mm")
public class Booking implements Comparable<Booking> {

	private String emp_id;

	@JsonFormat(pattern = "hh:mm")
	private LocalTime start_time;

	@JsonFormat(pattern = "hh:mm")
	private LocalTime end_time;

	@Override
	public int compareTo(Booking other) {
		boolean isOverlapping = BookingUtils.isOverlappingTime(this, other);
		if (isOverlapping) {
			return 0;
		} else {
			return this.getStart_time().compareTo(other.getStart_time());
		}
	}

}
