package com.sauloaraujo.timesheet.domain.project;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectRepositoryCustom {
	Page<Project> findByOptions(ProjectSearchOptions options, Pageable pageable);
}