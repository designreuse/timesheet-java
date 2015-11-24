package com.sauloaraujo.timesheet.domain.timesheet;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TimesheetPatchedEvent extends ApplicationEvent {
	private static final long serialVersionUID = -6832091618840903247L;

	private Timesheet timesheet;
	
	public TimesheetPatchedEvent(Object source) {
		super(source);
	}
}