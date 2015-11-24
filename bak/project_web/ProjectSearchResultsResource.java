package com.sauloaraujo.timesheet.web.project;

import java.util.List;

import com.sauloaraujo.timesheet.web.Resource;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProjectSearchResultsResource extends Resource {
	@Setter
	@Getter	
	public static class Embedded {
		private List<ProjectResource> projects;
	}

	private Embedded _embedded = new Embedded();	
}