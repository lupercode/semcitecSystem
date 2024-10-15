package com.luanpereira.semcitecsystem.controllers;

import com.luanpereira.semcitecsystem.models.UserModel;
import com.luanpereira.semcitecsystem.services.UserService;
import com.luanpereira.semcitecsystem.utils.BrazilianStates;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    private List<Map.Entry<String, String>> states = BrazilianStates.getStates();

    private static String USER_PROFILE_IMG_DIRECTORY;

    public UserController(@Value("${userProfileImgDirectory}") String userProfileImgDirectory) {
        USER_PROFILE_IMG_DIRECTORY = userProfileImgDirectory;
    }

    @GetMapping("/profile")
    public String myProfile(HttpSession session, Model model) {
        //Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserModel loggedUserData = userService.findById((UUID) session.getAttribute("ownerIdSession")).get();

        model.addAttribute("loggedUser", loggedUserData);
        model.addAttribute("content", "profile");

        return "default";
    }

    @GetMapping({"/userProfile/{uuid}", "/userProfile"})
    @PreAuthorize("hasRole('ADMIN') || #uuid == authentication.principal.uuid")
    public String userProfile(@PathVariable(required = false) UUID uuid, HttpSession session, Model model) {
        if (uuid == null) uuid = (UUID) session.getAttribute("ownerIdSession");
        UserModel userData = userService.findById(uuid).get();

        model.addAttribute("states", states);
        model.addAttribute("userData", userData);
        model.addAttribute("contentTitle", "Perfil do Funcionário");
        model.addAttribute("content", "profile");

        return "default";
    }

    @GetMapping("/listUsers")
    public String listUser(Model model) {
        List<UserModel> userList = userService.findAll();
        model.addAttribute("userList", userList);
        model.addAttribute("contentTitle", "Lista de Funcionários");
        model.addAttribute("content", "usersPage");
        return "default";
    }

    @GetMapping("/newUser")
    public String newUser(Model model) {
        model.addAttribute("states", states);
        model.addAttribute("contentTitle", "Novo Funcionário");
        model.addAttribute("content", "newUser");
        return "default";

    }

    @PostMapping("/saveUser")
    public String saveNewUser(RedirectAttributes redirectAttributes, Model model, UserModel userData) {
        String successMsg = "Usuário atualizado com sucesso";

        if (userData.getUuid() == null) {
            String encryptedPassword = new BCryptPasswordEncoder().encode(userData.getPassword());
            userData.setPassword(encryptedPassword);
            successMsg = "Novo usuário cadastrado com sucesso";
        } else {
            this.userService.findById(userData.getUuid()).ifPresent(userModel -> {
                userData.setUsername(userModel.getUsername());
                userData.setPassword(userModel.getPassword());
                userData.setImg(userModel.getImg());
                userData.setRole(userModel.getRole());
            });
            successMsg = "Usuário atualizado com sucesso";
        }

        try {
            this.userService.save(userData);
            redirectAttributes.addFlashAttribute("successMsg", successMsg);
            return "redirect:/user/userProfile/" + userData.getUuid();
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Funcionário já cadastrado");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Erro ao cadastrar funcionário");
        }

        return "redirect:/user/newUser";
    }

    @PostMapping("/changePassword")
    public String changePassword(
            RedirectAttributes redirectAttributes,
            UUID uuid,
            String currentPassword,
            String newPassword,
            String confirmPassword
    ) {
        UserModel registeredUser = this.userService.findById(uuid).orElse(new UserModel());
        String encryptedPassword = new BCryptPasswordEncoder().encode(newPassword);

        if (new BCryptPasswordEncoder().matches(currentPassword, registeredUser.getPassword()) && newPassword.equals(confirmPassword)) {
            registeredUser.setPassword(encryptedPassword);
            this.userService.save(registeredUser);
            redirectAttributes.addFlashAttribute("successMsg", "Senha alterada com sucesso");
            return "redirect:/user/userProfile/" + registeredUser.getUuid();
        } else {
            redirectAttributes.addFlashAttribute("errorMsg", "Erro ao alterar a senha");
            return "redirect:/user/userProfile/" + registeredUser.getUuid();
        }

    }

    @PostMapping("/saveImg")
    public String saveImg(MultipartFile img, UUID uuid, HttpSession session) {
        this.userService.findById(uuid).ifPresent(userModel -> {
            String filePath = "/userProfileImgDirectory/" + img.getOriginalFilename();
            userModel.setImg(filePath);
            this.userService.save(userModel);
            try {
                Files.copy(img.getInputStream(), Path.of(USER_PROFILE_IMG_DIRECTORY + img.getOriginalFilename()), StandardCopyOption.REPLACE_EXISTING);
                System.out.println(USER_PROFILE_IMG_DIRECTORY + img.getOriginalFilename());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (session.getAttribute("ownerIdSession").equals(uuid)) {
                session.setAttribute("ownerProfileImg", userModel.getImg());
            }
        });
        return "redirect:/user/userProfile/" + uuid;
    }
}
