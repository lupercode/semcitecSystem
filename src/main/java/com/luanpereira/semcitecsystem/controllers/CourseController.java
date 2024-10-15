package com.luanpereira.semcitecsystem.controllers;

import com.luanpereira.semcitecsystem.models.*;
import com.luanpereira.semcitecsystem.services.ClassroomService;
import com.luanpereira.semcitecsystem.services.CourseService;
import com.luanpereira.semcitecsystem.services.InscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/course")
public class CourseController {
    @Autowired
    private CourseService courseService;
    @Autowired
    private InscriptionService inscriptionService;
    @Autowired
    private ClassroomService classroomService;

    @GetMapping
    public String coursePage(Model model) {
        List<CourseModel> courseList = courseService.findAll();
        model.addAttribute("courseList", courseList);
        model.addAttribute("contentTitle", "Cursos");
        model.addAttribute("content", "coursePage");
        return "default";
    }

    @GetMapping("/newCourse")
    public String newCourse(Model model) {
        model.addAttribute("contentTitle", "Novo Curso");
        model.addAttribute("content", "newCourse");
        return "default";
    }

    @GetMapping("/courseProfile/{uuid}")
    public String courseProfile(@PathVariable final UUID uuid, Model model) {
        CourseModel course = this.courseService.findById(uuid);
        List<Classroom> classroomList = this.classroomService.findByCourseUuidOrderByName(uuid);

        model.addAttribute("classroomList", classroomList);
        model.addAttribute("course", course);
        model.addAttribute("contentTitle", "Perfil do Curso");
        model.addAttribute("content", "courseProfile");
        return "default";
    }

    @GetMapping("/classroomProfile/{uuid}")
    public String classroomProfile(@PathVariable final UUID uuid, Model model) {
        Classroom classroom = this.classroomService.findById(uuid);
        List<Inscription> inscriptionList = this.inscriptionService.findByClassroom(classroom);

        model.addAttribute("inscriptionList", inscriptionList);
        model.addAttribute("classroom", classroom);
        model.addAttribute("contentTitle", "Perfil da Turma");
        model.addAttribute("content", "classroomProfile");
        return "default";
    }

    @PostMapping("/saveClassroom")
    public String newClassroom(RedirectAttributes redirectAttributes, Classroom classroom) {
        if (classroom.getUuid() == null) {
            try {
                classroom.setStatus(Status.ATIVO);
                classroom.setClassroomCode("0");
                this.classroomService.save(classroom);
                redirectAttributes.addFlashAttribute("successMsg", "Nova turma criada com sucesso");
                String classroomCode = String.format("%04d%02d%06d",
                        classroom.getCreationDate().getYear(),
                        classroom.getCreationDate().getMonthValue(),
                        this.classroomService.getNextValFromSequence()
                );
                classroom.setClassroomCode(classroomCode);
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMsg", "Erro ao criar nova turma");
            }

        } else {
            Classroom savedClassroom = this.classroomService.findById(classroom.getUuid());
            classroom.setStatus(savedClassroom.getStatus());
            classroom.setClassroomCode(savedClassroom.getClassroomCode());
            classroom.setCreationDate(savedClassroom.getCreationDate());

        }
        this.classroomService.save(classroom);

        return "redirect:/course/courseProfile/" + classroom.getCourse().getUuid();
    }

    @PostMapping("/saveCourse")
    public String saveCourse(RedirectAttributes redirectAttributes, CourseModel courseDate) {
        String msg = courseDate.getUuid() == null ? "Curso criado com sucesso!" : "Curso atualizado com sucesso";
        try {
            this.courseService.save(courseDate);
            redirectAttributes.addFlashAttribute("successMsg", msg);
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Um curso com este nome já existe");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Erro ao criar o curso");
        }
    return "redirect:/course/courseProfile/" + courseDate.getUuid();
    }

    @GetMapping("/closeClassroom/{uuid}")
    public String closeClassroom(@PathVariable final UUID uuid) {
        Classroom classroom = this.classroomService.findById(uuid);
        List<Inscription> inscriptionsByClassroom = this.inscriptionService.findActiveInscriptionsByClassroom(classroom);
        inscriptionsByClassroom.stream()
                        .forEach(inscription -> {
                            inscription.setStatus(Status.FINALIZADO);
                        });
        classroom.setStatus(Status.FINALIZADO);
        classroom.setClosingDate(LocalDate.now());
        this.classroomService.save(classroom);
        return "redirect:/course/classroomProfile/" + uuid;
    }
}