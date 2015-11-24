package com.sauloaraujo.timesheet.web.task;

import com.sauloaraujo.timesheet.web.Resource;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskResource extends Resource {
    private String name;
    private String description;
}
