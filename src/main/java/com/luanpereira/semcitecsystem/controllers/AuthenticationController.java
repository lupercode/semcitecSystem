package com.luanpereira.semcitecsystem.controllers;

import com.luanpereira.semcitecsystem.models.UserModel;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthenticationController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/saveSession")
    public String userSession(HttpSession session) {

        UserModel loggedUserDetails = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        session.setAttribute("ownerSession", loggedUserDetails.getName());
        session.setAttribute("ownerIdSession", loggedUserDetails.getUuid());
        session.setAttribute("ownerProfileImg", loggedUserDetails.getImg());

        return "redirect:/index";
    }

}
