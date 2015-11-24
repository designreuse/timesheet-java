package com.sauloaraujo.timesheet.web.timesheet;

import com.sauloaraujo.timesheet.domain.timesheet.EntryCell;
import com.sauloaraujo.timesheet.domain.timesheet.ProjectRow;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.ResourceSupport;



import java.util.Date;
import java.util.List;

/**
 * Created by deinf.rcsilva on 13/11/2015.
 */
@Setter
@Getter
public class TimesheetResource extends ResourceSupport {
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
        public static class TaskRowDto {
            private int id;
            private String task;
            private List<EntryCell> entryCells;
        }
    }
}
