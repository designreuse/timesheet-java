package com.sauloaraujo.timesheet.web.timesheet;

import com.sauloaraujo.timesheet.domain.CalendarService;
import com.sauloaraujo.timesheet.domain.DateService;
import com.sauloaraujo.timesheet.domain.timesheet.Timesheet;
import com.sauloaraujo.timesheet.domain.timesheet.TimesheetService;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import org.springframework.web.bind.annotation.*;


import java.util.Calendar;
import java.util.Date;

/**
 * Created by deinf.rcsilva on 12/11/2015.
 */


// http://localhost:8080/api/timesheets/
@RestController
@RequestMapping("/api/timesheet")
public class TimesheetController {
    private static final String DEFAULT_DAYS = "7";

    private @Autowired TimesheetService service;
    private @Autowired MapperFacade mapper;
    private @Autowired DateService dateService;
    private @Autowired CalendarService calendarService;

    /*
    @RequestMapping(method= RequestMethod.GET, value="/today")
    public TimesheetResource get(
            @RequestParam(value="days", defaultValue=DEFAULT_DAYS) int days
            ) {
        return get(dateService.midnight(), days);
    }
    */

    @RequestMapping(method=RequestMethod.GET)
    public TimesheetResource get(
            @RequestParam(value="start", required = false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) Date start,
            @RequestParam(value="days", required = false, defaultValue=DEFAULT_DAYS) Integer days
           ) {

        if(start == null) {
            start = dateService.midnight();
        }

        // http://localhost:8080/api/timesheets/2010-01-01
        TimesheetResource resource = mapper.map(service.get(start, days), TimesheetResource.class);
        resource.add(
            linkTo(
                methodOn(getClass())
                    .get(start,days)
                ).withSelfRel()
        );
        resource.add(
                linkTo(
                    methodOn(getClass())
                        .patch(start, null)
                ).withRel("save")
        );

        Calendar calendar;
        calendar = calendarService.midnight(start);
        calendar.add(Calendar.DAY_OF_MONTH, -days);
        resource.add(
                linkTo(
                        methodOn(getClass())
                                .get(calendar.getTime(),days)
                ).withRel("previous")
        );
        calendar = calendarService.midnight(start);
        calendar.add(Calendar.DAY_OF_MONTH, +days);
        resource.add(
                linkTo(
                        methodOn(getClass())
                                .get(calendar.getTime(),days)
                ).withRel("next")
        );

        if (days > 1) {
            resource.add(
                    linkTo(
                            methodOn(getClass())
                                    .get(start, days - 1)
                    ).withRel("minus")
            );
        }

        resource.add(
                linkTo(
                        methodOn(getClass())
                                .get(start,days+1)
                ).withRel("plus")
        );

        return resource;
    }

    @RequestMapping(method= RequestMethod.PATCH, value="/{start}")
    public Object patch(
            @PathVariable("start") @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) Date start,
            @RequestBody TimesheetResource timesheetResource
        ) {
        Timesheet timesheet = mapper.map(timesheetResource, Timesheet.class);
        service.patch(start, timesheet);
        return null;
    }
}
