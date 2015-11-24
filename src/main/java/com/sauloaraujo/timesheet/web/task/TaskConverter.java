package com.sauloaraujo.timesheet.web.task;

import com.sauloaraujo.timesheet.domain.task.Task;
import com.sauloaraujo.timesheet.domain.task.TaskController;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.metadata.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;



@Component
public class TaskConverter extends CustomConverter<Task, TaskResource> {
    private @Autowired
    MapperFacade mapper;

    @Override
    public TaskResource convert(Task task, Type<? extends TaskResource> type) {
        TaskResource resource = new TaskResource();
        mapper.map(task,  resource);

        resource.add(
                linkTo(
                        methodOn(TaskController.class)
                                .get(task.getId())
                ).withSelfRel()
        );

        return resource;

    }
}
