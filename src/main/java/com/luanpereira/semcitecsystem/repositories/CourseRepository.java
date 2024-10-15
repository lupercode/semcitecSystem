package com.luanpereira.semcitecsystem.repositories;

import com.luanpereira.semcitecsystem.models.CourseModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CourseRepository extends JpaRepository<CourseModel, UUID> {
}
