package com.sauloaraujo.timesheet.web.project;

import com.sauloaraujo.timesheet.web.task.TaskResource;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.ResourceSupport;

import java.net.URI;
import java.util.List;

@Setter
@Getter
public class ProjectSearchFormResource extends ResourceSupport {
    private ProjectSearchOptionsDto options;

    private Embedded _embedded = new Embedded();

    @Setter
    @Getter
    public static class ProjectSearchOptionsDto {
        private String name;
        private String description;
        private List<URI> tasks;
    }

    @Setter
    @Getter
    public static class Embedded {
        private List<TaskResource> tasks;
    }
}
