package com.sauloaraujo.timesheet.web.timesheet;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sauloaraujo.timesheet.domain.CalendarService;
import com.sauloaraujo.timesheet.domain.timesheet.Timesheet;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

@Component
public class TimesheetConverter extends CustomConverter<Timesheet, TimesheetResource> {
	private @Autowired CalendarService calendarService;
	
	@Override
	public TimesheetResource convert(Timesheet source, Type<? extends TimesheetResource> destinationType) {
		TimesheetResource resource = new TimesheetResource();
		mapperFacade.map(source, resource);
		
		Date start = resource.getDates().get(0);
		int days = resource.getDates().size();		
		resource.add(linkTo(methodOn(TimesheetController.class).get(start, days)).withSelfRel());		
		
		Calendar calendar = calendarService.midnight(start);
		calendar.add(Calendar.DATE, -days);
		resource.add(linkTo(methodOn(TimesheetController.class).get(calendar.getTime(), days)).withRel("previous"));
		
		calendar = calendarService.midnight(start);
		calendar.add(Calendar.DATE, days);
		resource.add(linkTo(methodOn(TimesheetController.class).get(calendar.getTime(), days)).withRel("next"));
		
		if (days > 1) {
			resource.add(linkTo(methodOn(TimesheetController.class).get(start, days - 1)).withRel("minus"));	
		}
		
		resource.add(linkTo(methodOn(TimesheetController.class).get(start, days + 1)).withRel("plus"));
		
		return resource;
	}
}