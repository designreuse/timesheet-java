package com.sauloaraujo.timesheet.web;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sauloaraujo.timesheet.web.project.ProjectService;
import com.sauloaraujo.timesheet.web.project.ProjectController;
import com.sauloaraujo.timesheet.web.timesheet.TimesheetController;

@RestController
@RequestMapping("/api")
public class RootController {
	@RequestMapping(method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public Resource getRoot() {
		Resource root = new Resource();
		root.add(linkTo(methodOn(RootController.class).getRoot()).withSelfRel());
		root.add(linkTo(methodOn(ProjectController.class).getProjectForm(ProjectService.NEW_PROJECT_ID)).withRel("newProjectForm"));
		root.add(linkTo(methodOn(ProjectController.class).getProjectSearchOptionsForm(null, null, null)).withRel("projectSearchOptionsForm"));
		root.add(linkTo(methodOn(TimesheetController.class).get(7)).withRel("timesheet"));
		return root;
	}
}