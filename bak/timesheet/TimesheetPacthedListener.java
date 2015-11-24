package com.sauloaraujo.timesheet.web.timesheet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

import com.sauloaraujo.timesheet.domain.timesheet.TimesheetPatchedEvent;
import com.sauloaraujo.timesheet.web.configuration.WebSocketMessageBrokerConfigurer;

import ma.glasnost.orika.MapperFacade;

@Component
public class TimesheetPacthedListener implements ApplicationListener<TimesheetPatchedEvent> {
	private @Autowired SimpMessageSendingOperations template;
	private @Autowired MapperFacade mapperFacade;
	
	@Override
	public void onApplicationEvent(TimesheetPatchedEvent event) {
//		template.convertAndSendToUser(SecurityContextHolder.getContext().getAuthentication().getName(), WebSocketConfiguration.QUEUE + "/timesheet/patch", mapperFacade.map(event.getTimesheet(), TimesheetResource.class));
		template.convertAndSend(WebSocketMessageBrokerConfigurer.TOPIC + "/timesheet/patch", mapperFacade.map(event.getTimesheet(), TimesheetResource.class));
	}	
}