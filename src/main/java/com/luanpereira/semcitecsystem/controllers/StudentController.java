package com.luanpereira.semcitecsystem.controllers;

import com.luanpereira.semcitecsystem.models.Inscription;
import com.luanpereira.semcitecsystem.models.StudentModel;
import com.luanpereira.semcitecsystem.repositories.StudentRepository;
import com.luanpereira.semcitecsystem.services.InscriptionService;
import com.luanpereira.semcitecsystem.services.StudentService;
import com.luanpereira.semcitecsystem.utils.BrazilianStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private StudentService studentService;
    @Autowired
    private InscriptionService inscriptionService;

    private List<Map.Entry<String, String>> states = BrazilianStates.getStates();

    @GetMapping
    public String studentList(Model model) {
        List<StudentModel> studentList = this.studentRepository.findAll();
        model.addAttribute("studentList", studentList);
        model.addAttribute("contentTitle", "Alunos");
        model.addAttribute("content", "studentPage");
        return "default";
    }

    @GetMapping("/newStudent")
    public String newStudent(Model model) {
        model.addAttribute("states", states);

        model.addAttribute("contentTitle", "Novo Aluno");
        model.addAttribute("content", "newStudent");
        return "default";
    }

    @GetMapping("/studentProfile/{uuid}")
    private String studentProfile(@PathVariable final UUID uuid, Model model) {
        StudentModel student = this.studentRepository.findById(uuid).orElse(new StudentModel());
        List<Inscription> inscriptionsList = this.inscriptionService.findByStudent(student);

        model.addAttribute("states", states);
        model.addAttribute("inscriptionList", inscriptionsList);
        model.addAttribute("student", student);
        model.addAttribute("contentTitle", "Perfil do Aluno");
        model.addAttribute("content", "studentProfile");
        return "default";
    }

    @PostMapping("/saveDescription")
    private String saveDescription(RedirectAttributes redirectAttributes, StudentModel studentData) {
        this.studentService.saveDescription(studentData);
        redirectAttributes.addFlashAttribute("successMsg", "Aluno editado com sucesso");
        return "redirect:/student/studentProfile/" + studentData.getUuid();
    }

    @PostMapping("/saveStudent")
    public String saveStudent(RedirectAttributes redirectAttributes, StudentModel studentData) {
        String successMsg = studentData.getUuid() == null ? "Aluno criado com sucesso" : "Aluno editado com sucesso";
        try {
            this.studentService.save(studentData);
            redirectAttributes.addFlashAttribute("successMsg", successMsg);
            return "redirect:/student/studentProfile/" + studentData.getUuid();
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Aluno com este cpf já existe");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Algo deu errado");
        }
        return "redirect:/student/newStudent";
    }
}
