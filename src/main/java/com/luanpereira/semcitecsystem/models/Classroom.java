package com.luanpereira.semcitecsystem.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Classroom {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID uuid;
    @ManyToOne
    @JoinColumn(name = "course_uuid", nullable = false)
    private CourseModel course;
    private String name;
    @ColumnDefault("0")
    private int vacancies;
    private String room;
    private String days;
    private String hours;
    @CreationTimestamp
    private LocalDate creationDate;
    private LocalDate closingDate;
    @Enumerated(EnumType.STRING)
    @ColumnDefault("'ATIVO'")
    private Status status;
    @ColumnDefault("0")
    @Column(unique = true, nullable = false, length = 14)
    private String classroomCode;
    private String whatsappGroupLink;
    @Column(columnDefinition = "TEXT")
    private String description;
}
