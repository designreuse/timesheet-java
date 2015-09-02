package com.sauloaraujo.timesheet.rest;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sauloaraujo.timesheet.rest.project.ProjectController;

@RestController
@RequestMapping("/api")
public class RootController {
	@RequestMapping(method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResourceSupport getRoot() {
		ResourceSupport root = new ResourceSupport();
		root.add(linkTo(methodOn(RootController.class).getRoot()).withSelfRel());
		root.add(linkTo(methodOn(ProjectController.class).getProjectForm("new")).withRel("newProjectForm"));
		root.add(linkTo(methodOn(ProjectController.class).getProjectSearchOptionsForm(null, null, null)).withRel("projectSearchOptionsForm"));
		return root;
	}
}