package com.sauloaraujo.timesheet.domain.task;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskService {
	private @Autowired TaskRepository repository;
	
	public Task findOne(int id) {
		return repository.findOne(id);
	}
	
	public List<Task> findAll() {
		return repository.findAll(); 
	}
}