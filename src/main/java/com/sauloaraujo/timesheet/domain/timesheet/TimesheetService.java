package com.sauloaraujo.timesheet.domain.timesheet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

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
    private @Autowired ApplicationEventPublisher applicationEventPublisher;


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
				for (int column = 0; column < dates.size(); ++column) {
					Double time = null;
					Date date = dates.get(column);					
					for (Entry entry : entries) {
						if (entry.getProject().getId() == project.getId()
								&& entry.getTask().getId() == task.getId()
								&& dateService.sameDay(entry.getDate(), date)) {
							time = entry.getTime();
						}
					}

					EntryCell entryCell = new EntryCell();
					entryCell.setColumn(column);
					entryCell.setTime(time);
					entryCell.setDisabled(
						date.compareTo(dateService.midnight(project.getStartDate())) < 0
						|| project.getEndDate() != null
							&& date.compareTo(dateService.midnight(project.getEndDate())) > 0
					);
					entryCells.add(entryCell);
				}

				TaskRow taskRow = new TaskRow();
				taskRow.setTask(task);
				taskRow.setEntryCells(entryCells);
				taskRows.add(taskRow);
			}
			
			ProjectRow projectRow = new ProjectRow();
			projectRow.setProject(project);
			projectRow.setTaskRows(taskRows);
			projectRows.add(projectRow);
		}

		Timesheet timesheet = new Timesheet();
		timesheet.setDates(dates);
		timesheet.setProjectRows(projectRows);
		return timesheet;
	}

	public void patch(Date start, Timesheet timesheet) {
		List<Entry> entries = new ArrayList<>();
		for (ProjectRow projectRow : timesheet.getProjectRows()) {
			for (TaskRow taskRow : projectRow.getTaskRows()) {
				for (EntryCell entryCell : taskRow.getEntryCells()) {
					Calendar calendar = calendarService.midnight(start);
					calendar.add(Calendar.DATE, entryCell.getColumn());
					Date date = calendar.getTime();
					Entry entry = entryRepository.findByProjectIdAndTaskIdAndDate(projectRow.getProject().getId(), taskRow.getTask().getId(), date);
					if (entryCell.getTime() == null) {
						if (entry != null) {
							entryRepository.delete(entry);
						}
					} else {
						if (entry == null) {
							entry = new Entry();
							entry.setProject(projectRepository.findOne(projectRow.getProject().getId()));
							entry.setTask(taskRepository.findOne(taskRow.getTask().getId()));
							entry.setDate(date);
						}
						entry.setTime(entryCell.getTime());
						entries.add(entry);						
					}
				}
			}
		}
		entryRepository.save(entries);

        TimesheetPatchedEvent evt = new TimesheetPatchedEvent(this);



        List<Date> dates = new ArrayList<Date>();
        dates.add(start);
        timesheet.setDates(dates);

        evt.setTimeseet(timesheet);

        applicationEventPublisher.publishEvent(evt);
	}
}