package com.sauloaraujo.timesheet.web.timesheet;

import java.util.Date;
import java.util.List;

import com.sauloaraujo.timesheet.domain.timesheet.EntryCell;
import com.sauloaraujo.timesheet.web.Resource;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TimesheetResource extends Resource {
	private List<Date> dates;
	private List<ProjectRowDto> projectRows;
	
	@Setter
	@Getter
	public static class ProjectRowDto {
		private int id;
		private String project;
		private List<TaskRowDto> taskRows;
		
		@Setter
		@Getter
		public static class ProjectDto {
			private Integer id;
			private String name;
		}
		
		@Setter
		@Getter
		public static class TaskRowDto {
			private Integer id;
			private String task;
			private List<EntryCell> entryCells;
			
			@Setter
			@Getter
			public static class TaskDto {
				private Integer id;
				private String name;
			}
		}		
	}
}