package com.sauloaraujo.timesheet.web.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sauloaraujo.timesheet.domain.task.TaskService;

import ma.glasnost.orika.MapperFacade;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
	private @Autowired TaskService taskService;
	private @Autowired MapperFacade mapperFacade;
	
	@RequestMapping(method=RequestMethod.GET, value="/{id}")
	public TaskResource get(@PathVariable("id") int id) {
		return mapperFacade.map(taskService.findOne(id), TaskResource.class);
	}	
}