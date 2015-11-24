package com.sauloaraujo.timesheet.web.project;

import com.sauloaraujo.timesheet.domain.project.Project;
import com.sauloaraujo.timesheet.domain.project.ProjectSearchOptions;
import com.sauloaraujo.timesheet.domain.project.ProjectService;
import com.sauloaraujo.timesheet.domain.task.TaskController;
import com.sauloaraujo.timesheet.web.project.ProjectSearchFormResource.ProjectSearchOptionsDto;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.TemplateVariable;
import org.springframework.hateoas.TemplateVariables;
import org.springframework.hateoas.UriTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    private @Autowired ProjectService service;
    private @Autowired MapperFacade mapper;
    private @Autowired TaskController taskController;


    @RequestMapping(method= RequestMethod.GET, value="/search/form")
    public ProjectSearchFormResource getSearchForm(@RequestParam(value="name", required=false) String name,
                              @RequestParam(value="description", required=false) String description,
                              @RequestParam(value="tasks", required=false) List<URI> tasks)
    {
        ProjectSearchOptionsDto options = new ProjectSearchOptionsDto();
        options.setName(name);
        options.setDescription(description);
        options.setTasks(tasks);

        ProjectSearchFormResource resource = new ProjectSearchFormResource();
        resource.setOptions(options);
/*        resource.add(
                linkTo(
                        methodOn(getClass())
                                .get(calendar.getTime(),days)
                ).withRel("previous")
        );
*/
        resource.add(
                linkTo(
                        methodOn(getClass())
                                .getSearchResults(name, description, tasks, null, null)
                ).withRel("results")
        );
        resource.add(
                linkTo(
                        methodOn(getClass())
                                .getSearchForm(name, description, tasks)
                ).withSelfRel()
        );
        resource.add(
                getSearchResultsLink("otherResults")
        );

     /*
        resource.add(
                linkTo(
                        methodOn(TaskController.class)
                                .get()
                ).withRel("tasks")
        );
*/
        resource.get_embedded().setTasks(taskController.get());




        return resource;
    }

    private Link getSearchResultsLink(String rel) {
        String baseUri = linkTo(methodOn(getClass())
                .getSearchResults(null, null, null, null, null)).toString();

        List<TemplateVariable> variableList = new ArrayList<>();
        variableList.add(new TemplateVariable("name", TemplateVariable.VariableType.REQUEST_PARAM));
        variableList.add(new TemplateVariable("description", TemplateVariable.VariableType.REQUEST_PARAM));
        variableList.add(new TemplateVariable("tasks", TemplateVariable.VariableType.REQUEST_PARAM));
        variableList.add(new TemplateVariable("page", TemplateVariable.VariableType.REQUEST_PARAM));

        TemplateVariables variables = new TemplateVariables(variableList);
        UriTemplate template = new org.springframework.hateoas.UriTemplate(baseUri, variables);
        Link link = new Link(template, rel);
        return link;
    }


    @RequestMapping(method= RequestMethod.GET, value="/search/results")
    public ProjectSearchResultsResource getSearchResults(
            @RequestParam(value="name", required=false) String name,
            @RequestParam(value="description", required=false) String description,
            @RequestParam(value="tasks", required=false) List<URI> tasks,
            @RequestParam(value="page", required=false, defaultValue = "0") Integer page,
            @RequestParam(value="size", required=false, defaultValue = "2") Integer size)
    {
        ProjectSearchOptions options = new ProjectSearchOptions();
        options.setName(name);
        options.setDescription(description);
        // converter de URI para tarefas
        //options.setTasks(tasks);

        Pageable pageable =  new PageRequest(page, size);
        Page<Project> results = service.find(options, pageable);
        List<Project> projects = results.getContent();
        List<ProjectResoure> projectResources =
            mapper.mapAsList(projects, ProjectResoure.class);

        ProjectSearchResultsResource resource = new ProjectSearchResultsResource();
        resource.setProjects(projectResources);
        return resource;
    }



}
