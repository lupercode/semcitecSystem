package com.luanpereira.semcitecsystem.services;

import com.luanpereira.semcitecsystem.models.StudentModel;
import com.luanpereira.semcitecsystem.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    public Long countItem() {
        return studentRepository.count();
    }

    public StudentModel findById(UUID uuid) {
        return this.studentRepository.findById(uuid).get();
    }

    public List<StudentModel> findAll() {
        return this.studentRepository.findAll();
    }

    public StudentModel save(StudentModel studentData) {
        if (studentData.getUuid() != null) {
            StudentModel student = this.studentRepository.findById(studentData.getUuid()).get();
            studentData.setObs(student.getObs());
        }
        return this.studentRepository.save(studentData);
    }
    
    public StudentModel saveDescription(StudentModel studentData) {
        StudentModel student = this.studentRepository.findById(studentData.getUuid()).get();
        student.setObs(studentData.getObs());
        return this.studentRepository.save(student);
    }
}
