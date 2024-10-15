package com.luanpereira.semcitecsystem.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "officialDoc")
public class OfficialDocModel {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID uuid;
    @Column(unique = true, nullable = false)
    private String number;
    private String receiver;
    private String receiverPosition;
    private String receiverTitle;
    private String subject;
    @Column(columnDefinition = "TEXT")
    private String description;
    @ManyToOne
    @JoinColumn(name = "author_uuid")
    private UserModel author;
    @ManyToOne
    @JoinColumn(name = "modifier_uuid")
    private UserModel modifier;
    private LocalDate creationAt;
    private LocalDate changedAt;
    @Transient
    private MultipartFile file;
    private String filePath;

}
