package com.sauloaraujo.timesheet.web.project;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sauloaraujo.timesheet.web.project.Project;
import com.sauloaraujo.timesheet.web.project.ProjectSearchOptions;
import com.sauloaraujo.timesheet.web.project.ProjectService;
import com.sauloaraujo.timesheet.domain.task.Task;
import com.sauloaraujo.timesheet.domain.task.TaskService;
import com.sauloaraujo.timesheet.web.task.TaskResource;

import ma.glasnost.orika.MapperFacade;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
	private static final int FIRST_PAGE = 0;
	private static final int DEFAULT_SIZE = 2;

	private @Autowired ProjectService projectService;
	private @Autowired TaskService taskService;
	private @Autowired MapperFacade mapperFacade;

	@RequestMapping(method=RequestMethod.POST)
	public Object post(@RequestBody ProjectResource resource) {
		projectService.save(mapperFacade.map(resource, Project.class));
		return null;
	}

	@RequestMapping(method=RequestMethod.GET, value="/{id}")
	public ProjectResource get(@PathVariable("id") int id) {
		Project project;
		if (id == ProjectService.NEW_PROJECT_ID) {
			project = projectService.create();
		} else {
			project = projectService.findOne(id); 
		}		
		return mapperFacade.map(project, ProjectResource.class);
	}

	@RequestMapping(method=RequestMethod.PUT, value="/{id}")
	public Object put(@PathVariable("id") int id, @RequestBody ProjectResource resource) {
		Project project = mapperFacade.map(resource, Project.class);
		project.setId(id);
		projectService.save(project);
		return null;
	}

	@RequestMapping(method=RequestMethod.DELETE, value="/{id}")
	public Object delete(@PathVariable("id") int id) {
		projectService.delete(id);
		return null;
	}	

	@RequestMapping(method=RequestMethod.GET, value="/search/options/form")
	public ProjectSearchOptionsFormResource getProjectSearchOptionsForm(
			@RequestParam(value="name", required=false) String name,
			@RequestParam(value="description", required=false) String description,
			@RequestParam(value="tasks", required=false) List<URI> tasks) {
		ProjectSearchOptionsFormResource resource = new ProjectSearchOptionsFormResource();
		resource.get_embedded().setTasks(mapperFacade.mapAsList(taskService.findAll(), TaskResource.class));
		resource.add(linkTo(methodOn(ProjectController.class).getProjectSearchOptionsForm(name, description, tasks)).withSelfRel());
		resource.add(linkTo(methodOn(ProjectController.class).getProjectSearchResults(name, description, tasks, FIRST_PAGE, DEFAULT_SIZE)).withRel("results"));
		return resource;
	}

	@RequestMapping(method=RequestMethod.GET, value="/search/result")
	public ProjectSearchResultsResource getProjectSearchResults(
			@RequestParam(value="name", required=false) String name,
			@RequestParam(value="description", required=false) String description,
			@RequestParam(value="tasks", required=false) List<URI> tasks,
			@RequestParam(value="page") int page,
			@RequestParam(value="size") int size) {
		ProjectSearchOptions options = new ProjectSearchOptions();
		options.setName(name);
		options.setDescription(description);
		if (tasks != null) {
			options.setTasks(mapperFacade.mapAsList(tasks, Task.class));
		}
		Pageable pageable = new PageRequest(page, size);
		Page<Project> result = projectService.find(options, pageable);
		ProjectSearchResultsResource resource = new ProjectSearchResultsResource();
		resource.get_embedded().setProjects(mapperFacade.mapAsList(result.getContent(), ProjectResource.class));
		resource.add(linkTo(methodOn(ProjectController.class).getProjectSearchResults(name, description, tasks, page, size)).withSelfRel());
		for (int i = 0; i < result.getTotalPages(); ++i) {
			resource.add(linkTo(methodOn(ProjectController.class).getProjectSearchResults(name, description, tasks, i, size)).withRel(Integer.toString(i + 1)));	
		}
		return resource;
	}

	@RequestMapping(method=RequestMethod.GET, value="/{id}/form")
	public ProjectFormResource getProjectForm(@PathVariable("id") int id) {
		ProjectFormResource resource = new ProjectFormResource();
		if (id == ProjectService.NEW_PROJECT_ID) {
			resource.add(linkTo(methodOn(ProjectController.class).post(null)).withRel("save"));
		} else {
			resource.add(linkTo(methodOn(ProjectController.class).put(id, null)).withRel("save"));
			resource.add(linkTo(methodOn(ProjectController.class).delete(id)).withRel("delete"));
		}
		resource.get_embedded().setProject(get(id));
		resource.get_embedded().setTasks(mapperFacade.mapAsList(taskService.findAll(), TaskResource.class));
		resource.add(linkTo(methodOn(ProjectController.class).getProjectForm(id)).withSelfRel());		
		return resource;
	}	
}