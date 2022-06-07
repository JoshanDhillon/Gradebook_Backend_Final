package com.cst438.controllers;

import com.cst438.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Date;
import java.util.List;


@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class AssignmentController {

    @Autowired
    AssignmentRepository assignmentRepository;

    @Autowired
    AssignmentGradeRepository assignmentGradeRepository;

    @Autowired
    CourseRepository courseRepository;

    @PostMapping("/assignment/{id}")
    @Transactional
    public void addAssignment(@RequestBody AssignmentListDTO.AssignmentDTO assignmentDTO, @PathVariable("id") Integer courseId, @AuthenticationPrincipal OAuth2User principal) {

        String email = principal.getAttribute("email");

        // Check whether given email is in the Course table for given course
        Course c = courseRepository.findById(assignmentDTO.courseId).orElse(null);
        if (!c.getInstructor().equals(email)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "This user not have access to add an assignment for this course.");
        }

        Assignment assignment = new Assignment();   // Create new assignment model object and set fields
        assignment.setName(assignmentDTO.assignmentName);
        Course course = courseRepository.findCourseById(courseId);
        assignment.setCourse(course);
        Date date = Date.valueOf(assignmentDTO.dueDate); //converting string into sql date
        assignment.setDueDate(date);
        assignment.setNeedsGrading(1);
        assignmentRepository.save(assignment);
        System.out.println("Assignment Successfully Added");

    }

    @PutMapping("/assignment/{id}")
    @Transactional
    public void updateAssignmentName(@RequestBody AssignmentListDTO.AssignmentDTO assignmentDTO, @PathVariable("id") Integer assignmentId, @AuthenticationPrincipal OAuth2User principal) {

        String email = principal.getAttribute("email");  // user name
        checkAssignment(assignmentId, email);  // check that user name matches instructor email 

        // update the assignment name
        System.out.printf("%d %s %d\n", assignmentDTO.assignmentId, assignmentDTO.assignmentName, assignmentDTO.courseTitle, assignmentDTO.dueDate);

        Assignment assignment = assignmentRepository.findAssignmentById(assignmentId);
        assignment.setName(assignmentDTO.assignmentName);
        assignmentRepository.save(assignment);
    }

    @DeleteMapping("/assignment/{id}")
    @Transactional
    public void deleteAssignmentById(@PathVariable("id") Integer assignmentId, @AuthenticationPrincipal OAuth2User principal) {

        String email = principal.getAttribute("email");
        checkAssignment(assignmentId, email);

        Assignment assignment = assignmentRepository.findAssignmentById(assignmentId);
        // Check whether if there any grades corresponding to this assignment
        if (assignment.getAssignmentGrades().size() == 0) {
            assignmentRepository.delete(assignment);

            System.out.println("Assignment ID" + assignmentId + "Assignment Successfully deleted");
        } else {
            System.out.println("This Assignment has grades assigned. So can't delete");
        }
    }

    private Assignment checkAssignment(int assignmentId, String email) {
        // get assignment
        Assignment assignment = assignmentRepository.findById(assignmentId).orElse(null);
        if (assignment == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Assignment not found. " + assignmentId);
        }
        // check that user is the course instructor
        if (!assignment.getCourse().getInstructor().equals(email)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not Authorized. ");
        }

        return assignment;
    }
}
