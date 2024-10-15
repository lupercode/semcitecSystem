package com.luanpereira.semcitecsystem.controllers;

import com.luanpereira.semcitecsystem.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.util.List;

@Controller
public class Dashboard {

    @Autowired
    private UserService userService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private ClassroomService classroomService;
    @Autowired
    private StatisticsService statisticsService;

    @GetMapping({"/", "/index*", "/dashboard"})
    public String dashboard(@PathVariable(required = false) String page, Model model) {
        LocalDate today = LocalDate.now();
        List<StatisticsService.Statistics> statisticsSemcitec = statisticsService.semcitecStatistics();

        model.addAttribute("statistics", statisticsSemcitec);
        model.addAttribute("birthdaysOfTheMonth", userService.birthdaysOfTheMonth(today.getMonthValue()));
        model.addAttribute("userCountItem", userService.countItem());
        model.addAttribute("classroomCountItem", classroomService.countItem());
        model.addAttribute("courseCountItem", courseService.countItem());
        model.addAttribute("studentCountItem", studentService.countItem());
        model.addAttribute("contentTitle", "Dashboard");
        model.addAttribute("content", (page != null) ? page : "dashboard");
        return "default";
    }

}
