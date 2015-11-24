package com.sauloaraujo.timesheet.web.project;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sauloaraujo.timesheet.web.project.Project;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.metadata.Type;

@Component
public class ProjectResourceConverter extends CustomConverter<Project, ProjectResource> {
	private @Autowired MapperFacade mapperFacade;

	@Override
	public ProjectResource convert(Project source, Type<? extends ProjectResource> destinationType) {
		ProjectResource resource = new ProjectResource();
		mapperFacade.map(source, resource);
		resource.add(linkTo(methodOn(ProjectController.class).get(source.getId())).withSelfRel());
		resource.add(linkTo(methodOn(ProjectController.class).getProjectForm(source.getId())).withRel("form"));
		return resource;
	}
}