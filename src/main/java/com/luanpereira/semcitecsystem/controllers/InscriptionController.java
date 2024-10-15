package com.luanpereira.semcitecsystem.controllers;

import com.luanpereira.semcitecsystem.models.*;
import com.luanpereira.semcitecsystem.services.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequestMapping("/inscription")
public class InscriptionController {

    @Autowired
    private StudentService studentService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private ClassroomService classroomService;
    @Autowired
    private InscriptionService inscriptionService;
    @Autowired
    private UserService userService;

    @GetMapping("/newInscription/{uuid}")
    public String newInscription(@PathVariable final UUID uuid, Model model, HttpSession session) {

        UUID ownerId = (UUID) session.getAttribute("ownerIdSession");
        UserModel user = ownerId != null ? userService.findById(ownerId).get() : new UserModel();

        StudentModel student = this.studentService.findById(uuid);
        List<CourseModel> courseList = this.courseService.findAll();

        Map<String, List<Classroom>> classroomsPerCourseList = new HashMap<>();
        for (CourseModel course : courseList) {
            classroomsPerCourseList.put(course.getName(), this.classroomService.findByCourseUuidOrderByName(course.getUuid()));
        }

        model.addAttribute("classroomList", classroomsPerCourseList);
        model.addAttribute("courseList", courseList);
        model.addAttribute("student", student);
        model.addAttribute("user", user);
        model.addAttribute("contentTitle", "Matrícula");
        model.addAttribute("content", "newInscription");
        return "default";
    }

    @PostMapping("/saveInscription")
    public String saveInscription(Inscription inscriptionData, RedirectAttributes redirectAttributes) {
        String successMsg = "";

        List<Inscription> inscriptionsOfTheStudent = this.inscriptionService.findByStudent(inscriptionData.getStudent());

        int classroomVacancies = this.classroomService.findById(inscriptionData.getClassroom().getUuid()).getVacancies();
        long totalInscriptionPerClassroom = this.inscriptionService.findByClassroom(inscriptionData.getClassroom()).stream()
                .filter(inscription ->
                    inscription.getStatus() == Status.ATIVO || inscription.getStatus() == Status.PENDENTE
                ).count();

        boolean anyRestriction = inscriptionsOfTheStudent.stream()
                .anyMatch(inscription -> inscriptionData.getCourse().getName().equals(inscription.getCourse().getName()) ||
                                inscription.getStatus().equals(Status.ATIVO)
                );

        if (anyRestriction == true) {
            redirectAttributes.addFlashAttribute("errorMsg", "Aluno matriculado");
            return "redirect:/inscription/newInscription/" + inscriptionData.getStudent().getUuid();
        }

        if (totalInscriptionPerClassroom >= classroomVacancies) {
            successMsg = "Aluno cadastrado como Reserva. \nA turma atingiu a capacidade máxima de vagas disponíveis";
            inscriptionData.setStatus(Status.RESERVA);
        } else {
            successMsg = "Matrícula realizada com sucesso";
            inscriptionData.setStatus(Status.ATIVO);
        }


        try {
            inscriptionData.setInscriptionCode("0");
            this.inscriptionService.save(inscriptionData);
            redirectAttributes.addFlashAttribute("successMsg", successMsg);

            String inscriptionCode = String.format("%04d%02d%06d",
                    inscriptionData.getInscriptionDate().getYear(),
                    inscriptionData.getInscriptionDate().getMonthValue(),
                    this.inscriptionService.getNextValFromSequence());

            inscriptionData.setInscriptionCode(inscriptionCode);
            this.inscriptionService.save(inscriptionData);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Erro ao realizar a matrícula");
        }
        //return "redirect:/inscription/newInscription/" + inscriptionData.getStudent().getUuid();
        return "redirect:/student/studentProfile/" + inscriptionData.getStudent().getUuid();
    }
}
