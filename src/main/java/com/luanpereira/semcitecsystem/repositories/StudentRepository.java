package com.luanpereira.semcitecsystem.repositories;

import com.luanpereira.semcitecsystem.models.StudentModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StudentRepository extends JpaRepository<StudentModel, UUID> {
}
