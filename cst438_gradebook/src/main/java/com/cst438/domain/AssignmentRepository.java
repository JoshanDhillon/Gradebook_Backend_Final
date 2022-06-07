package com.cst438.domain;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface AssignmentRepository extends CrudRepository <Assignment, Integer> {

	@Query("select a from Assignment a where a.needsGrading=1 and a.dueDate < current_date and a.course.instructor= :email order by a.id")
	List<Assignment> findNeedGradingByEmail(@Param("email") String email);

	@Query("select a from Assignment a where a.id = :assignmentId")
	Assignment findAssignmentById(@Param("assignmentId") Integer assignmentId);

	@Query("select a from Assignment a where a.course.course_id = :courseId")
	List<Assignment> findAssignmentsByCourseId(@Param("courseId") Integer courseId);
}
