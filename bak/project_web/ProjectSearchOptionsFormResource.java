package com.sauloaraujo.timesheet.web.project;

import java.util.List;

import com.sauloaraujo.timesheet.web.Resource;
import com.sauloaraujo.timesheet.web.task.TaskResource;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProjectSearchOptionsFormResource extends Resource {
	@Setter
	@Getter	
	public static class Embedded {
		private List<TaskResource> tasks;
	}

	private Embedded _embedded = new Embedded();
}