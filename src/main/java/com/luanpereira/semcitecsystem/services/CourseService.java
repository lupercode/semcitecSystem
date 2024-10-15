package com.luanpereira.semcitecsystem.services;

import com.luanpereira.semcitecsystem.models.CourseModel;
import com.luanpereira.semcitecsystem.repositories.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepository;

    public Long countItem() {
        return courseRepository.count();
    }

    public List<CourseModel> findAll() {
        return this.courseRepository.findAll();
    }

    public CourseModel findById(UUID uuid) {
        return this.courseRepository.findById(uuid).get();
    }

    public CourseModel save(CourseModel course) {
        return this.courseRepository.save(course);
    }
}
