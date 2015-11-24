package com.sauloaraujo.timesheet.web.timesheet;

import com.sauloaraujo.timesheet.domain.timesheet.Timesheet;
import com.sauloaraujo.timesheet.domain.timesheet.TimesheetPatchedEvent;
import com.sauloaraujo.timesheet.web.configuration.WebSocketMessageBrokerConfigurer;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

/**
 * Created by DEINF.RSOARES on 24/11/2015.
 */
@Component
public class TimesheetPatchedListener
        implements ApplicationListener<TimesheetPatchedEvent>{

    @Autowired
    private MapperFacade mapperFacade;

    @Autowired
    private SimpMessageSendingOperations template;

    @Override
    public void onApplicationEvent(TimesheetPatchedEvent timesheetPatchedEvent) {
        System.out.println("timesheetPatchedEvent");
        System.out.println(timesheetPatchedEvent);

        Timesheet timesheet = timesheetPatchedEvent.getTimeseet();

        TimesheetResource resource = mapperFacade.map(timesheet,TimesheetResource.class);

        template.convertAndSend(WebSocketMessageBrokerConfigurer.TOPIC + "/timesheet/patch",
                resource);

    }
}
