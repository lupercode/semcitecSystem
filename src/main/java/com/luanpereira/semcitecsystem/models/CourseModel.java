package com.luanpereira.semcitecsystem.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "course")
public class CourseModel {
    @Id
    @GeneratedValue(generator = "UUID")
    public UUID uuid;
    @Column(unique = true)
    public String name;
    public String entity;
    public String workload;
    @Column(columnDefinition = "TEXT")
    public String description;
    @CreationTimestamp
    public LocalDate creationDate;
}
