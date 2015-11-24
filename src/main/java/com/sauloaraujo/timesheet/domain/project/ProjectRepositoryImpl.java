package com.sauloaraujo.timesheet.domain.project;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QueryDslRepositorySupport;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import ma.glasnost.orika.MapperFacade;

@Repository
public class ProjectRepositoryImpl extends QueryDslRepositorySupport implements ProjectRepositoryCustom {
	private @Autowired MapperFacade mapperFacade;

	public ProjectRepositoryImpl() {
		super(Project.class);
	}

/*	
	@Override
	public Page<Project> findByOptions(ProjectSearchOptions options, Pageable pageable) {
		JPQLQuery query = from(QProject.project);				
		if (StringUtils.hasLength(options.getName())) {
			query.where(QProject.project.name.containsIgnoreCase(options.getName()));
		}		
		if (StringUtils.hasLength(options.getDescription())) {
			query.where(QProject.project.description.containsIgnoreCase(options.getDescription()));
		}		
		if (options.getTasks() != null) {
			query.where(
				new JPASubQuery()
					.from(QTask.task)
					.where(
						QTask.task.in(QProject.project.tasks),
						QTask.task.id.in(mapperFacade.mapAsList(options.getTasks(), Integer.class))
					).exists()
			);
		}
		long total = query.count();
		query.orderBy(QProject.project.name.toLowerCase().asc());
		query.limit(pageable.getPageSize());
		query.offset(pageable.getOffset());
		return new PageImpl<Project>(query.list(QProject.project), pageable, total);
	}
*/

	private @PersistenceContext EntityManager manager;

	public Page<Project> findByOptions(ProjectSearchOptions options, Pageable pageable) {
		StringBuilder jpql = new StringBuilder("from Project p where 1 = 1");

		if (StringUtils.hasLength(options.getName())) {
			jpql.append(" and lower(p.name) like :name");
		}
		if (StringUtils.hasLength(options.getDescription())) {
			jpql.append(" and lower(p.description) like :description");
		}
		if (options.getTasks() != null) {
			jpql.append(" and exists (select t from Task t where t member of p.tasks and t.id in (:tasks))");
		}

		long total = createQuery("select count(*) " + jpql, Long.class, options, null).getSingleResult();
		List<Project> content = createQuery("select p " + jpql + " order by p.name", Project.class, options, pageable).getResultList();
		return new PageImpl<Project>(content, pageable, total);
	}

	private <Result> TypedQuery<Result> createQuery(String jpql, Class<Result> resultClass, ProjectSearchOptions options, Pageable pageable) {
		TypedQuery<Result> query = manager.createQuery(jpql, resultClass);

		if (StringUtils.hasLength(options.getName())) {
			query.setParameter("name", "%" + options.getName().toLowerCase() + "%");
		}
		if (StringUtils.hasLength(options.getDescription())) {
			query.setParameter("description", "%" + options.getDescription().toLowerCase() + "%");
		}
		if (options.getTasks() != null) {
			query.setParameter("tasks", mapperFacade.mapAsList(options.getTasks(), Integer.class));
		}

		if (pageable != null) {
			query.setFirstResult(pageable.getOffset());
			query.setMaxResults(pageable.getPageSize());
		}

		return query;
	}
}