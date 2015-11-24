package com.sauloaraujo.timesheet.web.task;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sauloaraujo.timesheet.domain.task.Task;
import com.sauloaraujo.timesheet.domain.task.TaskService;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

@Component
public class TaskUriConverter extends BidirectionalConverter<Task, URI> {
	private @Autowired TaskService taskService;	

	@Override
	public URI convertTo(Task source, Type<URI> destinationType) {
		return linkTo(methodOn(TaskController.class).get(source.getId())).toUri();
	}

	@Override
	public Task convertFrom(URI source, Type<Task> destinationType) {
		String uri = source.toString();		
		Integer id = Integer.parseInt(uri.substring(uri.lastIndexOf("/") + 1));
		return taskService.findOne(id);
	}
}