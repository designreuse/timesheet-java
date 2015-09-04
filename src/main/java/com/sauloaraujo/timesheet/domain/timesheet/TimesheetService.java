package com.sauloaraujo.timesheet.domain.timesheet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sauloaraujo.timesheet.domain.CalendarService;
import com.sauloaraujo.timesheet.domain.DateService;
import com.sauloaraujo.timesheet.domain.entry.Entry;
import com.sauloaraujo.timesheet.domain.entry.EntryRepository;
import com.sauloaraujo.timesheet.domain.project.Project;
import com.sauloaraujo.timesheet.domain.project.ProjectRepository;
import com.sauloaraujo.timesheet.domain.task.Task;
import com.sauloaraujo.timesheet.domain.task.TaskRepository;

@Service
public class TimesheetService {
	private @Autowired EntryRepository entryRepository;
	private @Autowired ProjectRepository projectRepository;
	private @Autowired TaskRepository taskRepository;	
	private @Autowired CalendarService calendarService;
	private @Autowired DateService dateService;
	
	public Timesheet get(Date start, int days) {
		List<Date> dates = new ArrayList<>();
		Calendar calendar = calendarService.midnight(start);
		for (int i = 0; i < days; ++i) {
			dates.add(calendar.getTime());
			calendar.add(Calendar.DATE, 1);			
		}
		start = dates.get(0);
		Date end = dates.get(dates.size() - 1);

		List<Entry> entries = entryRepository.findByDateBetween(start, end);

		List<ProjectRow> projectRows = new ArrayList<>();
		for (Project project : projectRepository.findByStartDateLessThanEqual(end)) {
			List<TaskRow> taskRows = new ArrayList<>();
			for (Task task : project.getTasks()) {
				List<EntryCell> entryCells = new ArrayList<>();				
				for (int id = 0; id < dates.size(); ++id) {
					Double time = null;
					Date date = dates.get(id);					
					for (Entry entry : entries) {
						if (entry.getProject().getId().equals(project.getId())
								&& entry.getTask().getId().equals(task.getId())
								&& dateService.sameDay(entry.getDate(), date)) {
							time = entry.getTime();
						}
					}

					EntryCell entryCell = new EntryCell();
					entryCell.setId(id);
					entryCell.setTime(time);
					entryCell.setDisabled(date.compareTo(dateService.midnight(project.getStartDate())) < 0);
					entryCells.add(entryCell);
				}

				TaskRow taskRow = new TaskRow();
				taskRow.setId(task.getId());
				taskRow.setTask(task.getName());
				taskRow.setEntryCells(entryCells);
				taskRows.add(taskRow);
			}
			
			ProjectRow projectRow = new ProjectRow();
			projectRow.setId(project.getId());
			projectRow.setProject(project.getName());
			projectRow.setTaskRows(taskRows);
			projectRows.add(projectRow);
		}

		Timesheet timesheet = new Timesheet();
		timesheet.setDates(dates);
		timesheet.setProjectRows(projectRows);
		return timesheet;
	}

	@Transactional
	public void patch(Date start, Timesheet timesheet) {
		List<Entry> entries = new ArrayList<>();
		for (ProjectRow projectRow : timesheet.getProjectRows()) {
			for (TaskRow taskRow : projectRow.getTaskRows()) {
				for (EntryCell entryCell : taskRow.getEntryCells()) {
					Calendar calendar = calendarService.midnight(start);
					calendar.add(Calendar.DATE, entryCell.getId());
					Date date = calendar.getTime();
					Entry entry = entryRepository.findByProjectIdAndTaskIdAndDate(projectRow.getId(), taskRow.getId(), date);
					if (entry == null) {
						entry = new Entry();
						entry.setProject(projectRepository.findOne(projectRow.getId()));
						entry.setTask(taskRepository.findOne(taskRow.getId()));
						entry.setDate(date);						
					}
					entry.setTime(entryCell.getTime());
					entries.add(entry);
				}
			}
		}
		entryRepository.save(entries);
	}
}