package com.luanpereira.semcitecsystem.services;

import com.luanpereira.semcitecsystem.models.Classroom;
import com.luanpereira.semcitecsystem.repositories.ClassroomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ClassroomService {
    @Autowired
    private ClassroomRepository classroomRepository;

    public Long countItem() {
        return classroomRepository.count();
    }

    public List<Classroom> findAllOrderedByName() {
        Sort sort = Sort.by(Sort.Order.by("name"));
        return classroomRepository.findAll(sort);
    }

    public List<Classroom> findByCourseUuidOrderByName(UUID uuid) {
        return this.classroomRepository.findByCourseUuidOrderByName(uuid);
    }

    public Classroom findById(UUID uuid) {
        return this.classroomRepository.findById(uuid).get();
    }

    public Classroom save(Classroom classroom) {
        return this.classroomRepository.save(classroom);
    }

    public int getNextValFromSequence() {
        return this.classroomRepository.getNextValFromSequence();
    }
}
