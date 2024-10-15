package com.luanpereira.semcitecsystem.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "employee")
public class Employee {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID uuid;
    private String name;
    private String rg;
    @Column(unique = true)
    private String cpf;
    private String voterId;
    private LocalDate birthday;
    private String gender;
    private String workspace;
    private String position;
    private String workSchedule;
    private String matricula;
    private Status status;
    private String phone1;
    private String phone2;
    private String email;
    @ColumnDefault(value = "'s/n'")
    @Column(length = 20)
    private String houseNumber;
    private String street;
    private String neighborhood;
    private String city;
    private String state;
    private String zipCode;
    @Column(columnDefinition = "TEXT")
    private String obs;
    private String img;
}