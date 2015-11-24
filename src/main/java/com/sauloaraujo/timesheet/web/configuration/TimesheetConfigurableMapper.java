package com.sauloaraujo.timesheet.web.configuration;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.sauloaraujo.timesheet.domain.timesheet.ProjectRow;
import com.sauloaraujo.timesheet.domain.timesheet.TaskRow;
import com.sauloaraujo.timesheet.web.timesheet.TimesheetResource.ProjectRowDto;
import com.sauloaraujo.timesheet.web.timesheet.TimesheetResource.ProjectRowDto.TaskRowDto;

import ma.glasnost.orika.Converter;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;

@Component
public class TimesheetConfigurableMapper extends ConfigurableMapper {
	private @Autowired ApplicationContext context;

	public TimesheetConfigurableMapper() {
		super(false);
	}
	
	@Override
	@PostConstruct
	protected void init() {
		super.init();
	}
		
	@Override
	protected void configure(MapperFactory factory) { 
		Map<String, Converter> converters = context.getBeansOfType(Converter.class);
		for (Converter<?, ?> converter : converters.values()) {
			factory.getConverterFactory().registerConverter(converter);
		}
		factory.classMap(ProjectRow.class, ProjectRowDto.class)
			.field("project.id", "id")
			.field("project.name", "project")
			.byDefault()   // os demais atributos, usar a estratégia padrão
		.register();

		factory.classMap(TaskRow.class, TaskRowDto.class)
			.field("task.id", "id")
			.field("task.name", "task")
			.byDefault()
		.register();
	}
}