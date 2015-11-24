package com.sauloaraujo.timesheet.domain.task;

import com.sauloaraujo.timesheet.web.task.TaskResource;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private @Autowired TaskService service;
    private @Autowired MapperFacade mapperFacade;


    @RequestMapping(method = RequestMethod.GET, value ="/{id}")
    public TaskResource get(@PathVariable("id") int id){
        return mapperFacade.map(
                service.findOne(id),
                TaskResource.class
        );
    }


    @RequestMapping(method = RequestMethod.GET)
    public List<TaskResource> get(){
        return mapperFacade.mapAsList(
                service.findAll(),
                TaskResource.class
        );
    }
}
