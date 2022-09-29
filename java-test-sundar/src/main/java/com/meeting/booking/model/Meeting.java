package com.meeting.booking.model;

import java.util.List;

import org.joda.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Meeting {

	private LocalDate date;

	private List<Booking> bookings;
}
