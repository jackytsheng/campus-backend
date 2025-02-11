package com.rmr101.campus.service;

import com.rmr101.campus.dto.student.*;
import com.rmr101.campus.dto.teacher.TeacherGetResponse;
import com.rmr101.campus.dto.user.UserChangePasswordRequest;
import com.rmr101.campus.entity.Course;
import com.rmr101.campus.entity.Student;
import com.rmr101.campus.entity.Teacher;
import com.rmr101.campus.exception.BadParameterException;
import com.rmr101.campus.exception.InvalidIdException;
import com.rmr101.campus.mapper.CourseMapper;
import com.rmr101.campus.mapper.StudentAssignmentMapper;
import com.rmr101.campus.mapper.StudentMapper;
import com.rmr101.campus.repository.StudentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private StudentAssignmentMapper studentAssignmentMapper;

    //Get API
    public ArrayList<StudentGetResponse> getAllStudents(){
        ArrayList<StudentGetResponse> studentList= new ArrayList<StudentGetResponse>();
        studentRepository.findAll().forEach(student ->
                studentList.add(studentMapper.studentToStudentGetResponse(student)));
        return studentList;
    }

    public List<StudentGetResponse> findStudentBy(String name, String campusId) {
        if(campusId != null){
            List<StudentGetResponse> studentList = new ArrayList<StudentGetResponse>();
            Optional<Student> student = studentRepository.findByCampusId(campusId);
            if(student.isPresent()){
                studentList.add(studentMapper.studentToStudentGetResponse(student.get()));
                return studentList;
            }
            return studentList;
        }
        if(name != null){
            String firstName, lastName;
            String[] keywords = name.split("\\s+");
            switch(keywords.length){
                case 1:
                    firstName = lastName = "%" + keywords[0] + "%";
                    break;
                case 2:
                    firstName = "%" + keywords[0] + "%";
                    lastName = "%" + keywords[1] + "%";
                    break;
                default:
                    throw new BadParameterException("Value of Parameter name is valid!");
            }

            List<Student> resultStudents =
                    studentRepository.findByFirstNameLikeOrLastNameLike(firstName,lastName);
            return studentMapper.studentToStudentGetResponse(resultStudents);
        }
        return null;
    }

    public StudentGetDetails getStudentDetailsByID(UUID uuid, String detail) {
        Student student =  studentRepository.findById(uuid).orElseThrow(()-> new InvalidIdException("Student uuid doesn't exist."));
        StudentGetDetails studentDetails = new StudentGetDetails();
        studentDetails.setStudentInfo(studentMapper.studentToStudentGetResponse(student));

        if(detail != null) {
            if (detail.equals("courses")) {
                List<Course> courseList = new ArrayList<Course>();
                student.getCourses().forEach(
                        (studentCourse) -> courseList.add(studentCourse.getCourse()));
                studentDetails.setCourseList(
                        courseMapper.courseToCourseGetResponse(courseList));
                return studentDetails;
            }

            if (detail.equals("assignments")) {
                studentDetails.setAssignmentList(
                        studentAssignmentMapper.studentAssignmentToStudentAssignmentGetResponse(student.getAssignments()));
                return studentDetails;
            }

            if (detail.equals("all")) {
                List<Course> courseList = new ArrayList<Course>();
                student.getCourses().forEach(
                        (studentCourse) -> courseList.add(studentCourse.getCourse()));
                studentDetails.setCourseList(
                        courseMapper.courseToCourseGetResponse(courseList));
                studentDetails.setAssignmentList(
                        studentAssignmentMapper.studentAssignmentToStudentAssignmentGetResponse(student.getAssignments()));
                return studentDetails;
            }
        }

        return studentDetails;
    }

    //Post API
//    public StudentPostResponse addStudent(StudentPostRequest request) {
//        Student student =  studentMapper.studentPostRequestToStudent(request);
//        studentRepository.save(student);
//        return studentMapper.studentToStudentPostResponse(student);
//    }

    protected void addStudent(UUID uuid, String campusId, String firstName, String lastName){
        Student student = new Student();
        student.setUuid(uuid);
        student.setCampusId(campusId);
        student.setFirstName(firstName);
        student.setLastName(lastName);
        studentRepository.save(student);
        log.debug("Student: " + firstName +" "+lastName + " created with uuid:" + uuid);
    }

    public void changePassword(UUID uuid, UserChangePasswordRequest request) {
        userService.changePassword(uuid, request);
    }

    //Put API
    public void updateStudent(UUID uuid, StudentUpdateRequest request) {
        //validate uuid
        Student student = this.validateUuid(uuid);
        student.setAvatar(request.getAvatar());
        studentRepository.save(student);
    }

    //Delete API
    public void deleteStudent(UUID uuid) {
        studentRepository.findById(uuid).orElseThrow(()->
                new InvalidIdException());
        studentRepository.deleteById(uuid);
    }

    public Student validateUuid(UUID studentUuid){
        return studentRepository.findById(studentUuid).orElseThrow(()-> new InvalidIdException("Student uuid doesn't exist."));
    }

    public void setStudentInactive(UUID studentUuid){
        Student student = studentRepository.findById(studentUuid).orElseThrow(()-> new InvalidIdException("Student uuid doesn't exist."));
        student.setActive(false);
        studentRepository.save(student);
    }
}
