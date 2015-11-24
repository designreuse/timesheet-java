package com.sauloaraujo.timesheet.domain.project;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;

import com.sauloaraujo.timesheet.domain.DateService;
import com.sauloaraujo.timesheet.domain.ErrorsException;
import com.sauloaraujo.timesheet.domain.task.Task;

@Service
public class ProjectService {
	public static final int NEW_PROJECT_ID = 0;
	
	private @Autowired ProjectRepository repository;	
	private @Autowired DateService dateService;
	private @Autowired ProjectValidator validator;
	
	public Project create() {
		Project project = new Project();
		project.setStartDate(dateService.midnight());
		project.setTasks(new ArrayList<Task>());
		return project;
	}	

	public Project save(Project project) {
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(project, "project");
        validator.validate(project, errors);
        if (errors.hasErrors()) {
        	throw new ErrorsException(errors);
        }
		return repository.save(project);
	}
	
	public void delete(int id) {
		repository.delete(id);
	}	

	public Project findOne(int id) {
		return repository.findOne(id);
	}

	public Page<Project> find(ProjectSearchOptions options, Pageable pageable) {
		return repository.findByOptions(options, pageable);
	}
}