package com.sauloaraujo.timesheet.domain.project;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Integer>, ProjectRepositoryCustom {
	Project findByNameIgnoreCase(String name);
	List<Project> findByStartDateLessThanEqual(Date date);	
}