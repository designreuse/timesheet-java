package com.sauloaraujo.timesheet.web.project;

import java.net.URI;
import java.util.Date;
import java.util.List;

import com.sauloaraujo.timesheet.web.Resource;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProjectResource extends Resource {
	private String name;
	private String description;
	private Date startDate;
	private Date endDate;
	private List<URI> tasks;
}