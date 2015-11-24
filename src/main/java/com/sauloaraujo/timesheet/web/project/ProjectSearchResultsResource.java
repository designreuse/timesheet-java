package com.sauloaraujo.timesheet.web.project;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ProjectSearchResultsResource {
    private List<ProjectResoure> projects;
}
