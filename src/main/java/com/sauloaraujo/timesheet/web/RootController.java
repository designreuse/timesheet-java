package com.sauloaraujo.timesheet.web;

import com.sauloaraujo.timesheet.domain.timesheet.Timesheet;
import com.sauloaraujo.timesheet.web.project.ProjectController;
import com.sauloaraujo.timesheet.web.timesheet.TimesheetController;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by deinf.rcsilva on 19/11/2015.
 */
@RestController
@RequestMapping("/api")
public class RootController {
    @RequestMapping(method= RequestMethod.GET)
    public ResourceSupport get() {
        ResourceSupport resource = new ResourceSupport();

        resource.add(
                ControllerLinkBuilder.linkTo(
                        ControllerLinkBuilder.methodOn(getClass())
                        .get()
                ).withSelfRel()
        );

        resource.add(
                ControllerLinkBuilder.linkTo(
                        ControllerLinkBuilder.methodOn(TimesheetController.class)
                                .get(null, null)
                ).withRel("timesheet")
        );

        resource.add(
                ControllerLinkBuilder.linkTo(
                        ControllerLinkBuilder.methodOn(ProjectController.class)
                                .getSearchForm(null,null,null)
                ).withRel("projectSearchForm")
        );

        return resource;
    }

}
