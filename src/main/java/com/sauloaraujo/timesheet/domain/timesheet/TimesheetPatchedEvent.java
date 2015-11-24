package com.sauloaraujo.timesheet.domain.timesheet;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

/**
 * Created by DEINF.RSOARES on 24/11/2015.
 */
@Getter
@Setter
public class TimesheetPatchedEvent extends ApplicationEvent{
    private Timesheet timeseet;

    public TimesheetPatchedEvent(Object source) {
        super(source);
    }

}
