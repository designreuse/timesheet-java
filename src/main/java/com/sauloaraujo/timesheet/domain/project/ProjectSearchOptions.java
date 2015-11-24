package com.sauloaraujo.timesheet.domain.project;

import java.util.List;

import com.sauloaraujo.timesheet.domain.task.Task;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProjectSearchOptions {
	private String name;
	private String description;
	private List<Task> tasks;
}