package com.luanpereira.semcitecsystem.controllers;

import com.luanpereira.semcitecsystem.models.OfficialDocModel;
import com.luanpereira.semcitecsystem.models.UserModel;
import com.luanpereira.semcitecsystem.repositories.OfficialDocRepository;
import com.luanpereira.semcitecsystem.repositories.UserRepository;
import com.luanpereira.semcitecsystem.services.AccentRemover;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/officialDoc")
public class OfficialDocController {

    @Autowired
    private OfficialDocRepository officialDocRepository;
    @Autowired
    private UserRepository userRepository;

    private static String OFFICIAL_DOC_DIRECTORY;

    public OfficialDocController(@Value("${officialDocDirectory}") String officialDocDirectory) {
        OFFICIAL_DOC_DIRECTORY = officialDocDirectory;
    }

    @GetMapping
    public String OfficialDocPage(Model model) {
        List<OfficialDocModel> officialDocList = officialDocRepository.findAll();
        model.addAttribute("contentTitle", "Ofícios");
        model.addAttribute("content", "officialDoc");
        model.addAttribute("officialDocList", officialDocList);
        return "default";
    }

    @GetMapping("/edit/{uuid}")
    public String OfficialDocEdit(@PathVariable("uuid") final UUID uuid, Model model) {
        OfficialDocModel officialDocData = officialDocRepository.findFirstByUuid(uuid);
        UserModel author =  (officialDocData.getAuthor() != null) ? userRepository.findById(officialDocData.getAuthor().getUuid())
                .orElse(new UserModel()) : new UserModel();
        UserModel modifier =  (officialDocData.getModifier() != null) ? userRepository.findById(officialDocData.getModifier().getUuid())
                .orElse(new UserModel()) : new UserModel();

        model.addAttribute("author", author.getName());
        model.addAttribute("modifier", modifier.getName());
        model.addAttribute("changedAt", officialDocData.getChangedAt());
        model.addAttribute("officialDoc", officialDocData);
        model.addAttribute("contentTitle", "Editar Ofício");
        model.addAttribute("content", "officialDocNewEdit");

        return "default";
    }

    @GetMapping("/new")
    public String OfficialDocNew(Model model) {
        OfficialDocModel officialDoc = new OfficialDocModel();
        model.addAttribute("officialDoc", officialDoc);
        model.addAttribute("contentTitle", "Novo Ofício");
        model.addAttribute("content", "officialDocNewEdit");
        return "default";
    }

    @PostMapping("/save")
    public String OfficialDocSave(@ModelAttribute OfficialDocModel officialDocData, HttpSession session, RedirectAttributes redirectAttributes) throws IOException {

        UUID ownerId = (UUID) session.getAttribute("ownerIdSession");
        UserModel user = ownerId != null ? userRepository.findById(ownerId).orElse(new UserModel()) : new UserModel();

        if(officialDocData.getUuid() == null) {
            officialDocData.setAuthor(user);
            if (officialDocData.getCreationAt() == null) officialDocData.setCreationAt(LocalDate.now());

            try {
                officialDocData.setNumber("0");
                this.officialDocRepository.save(officialDocData);
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMsg", "Não foi possível criar o ofício");
                return "redirect:/officialDoc/new";
            }

            int seq = this.officialDocRepository.getNextValFromSequence();
            String seqFormatted = String.format("%04d", seq);
            String docNumber = seqFormatted + "/" + officialDocData.getCreationAt().getYear();

            officialDocData.setNumber(docNumber);
            redirectAttributes.addFlashAttribute("successMsg", "Ofício criado com sucesso");
        } else {
            OfficialDocModel officialDocFromDb = this.officialDocRepository.findById(officialDocData.getUuid()).orElse(null);
            officialDocData.setAuthor(officialDocFromDb.getAuthor());
            officialDocData.setModifier(user);
            officialDocData.setChangedAt(LocalDate.now());
            redirectAttributes.addFlashAttribute("successMsg", "Ofício editado com sucesso");
        }

        //Files.copy(officialDocData.getFile().getInputStream(), caminhoDestino, StandardCopyOption.REPLACE_EXISTING);
        try {
            if (officialDocData.getFile().isEmpty() == false) {
                MultipartFile file = officialDocData.getFile();
                String fileName = officialDocData.getNumber().replace('/', '-') + "_" + officialDocData.getSubject() + "_" + file.getOriginalFilename();
                String fileNameNormalized = AccentRemover.accentRemover(fileName);
                Files.copy(file.getInputStream(), Path.of(OFFICIAL_DOC_DIRECTORY + fileNameNormalized).normalize(), StandardCopyOption.REPLACE_EXISTING);
                officialDocData.setFilePath(OFFICIAL_DOC_DIRECTORY + fileNameNormalized);
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
        }

        this.officialDocRepository.save(officialDocData);

        return "redirect:/officialDoc/edit/" + officialDocData.getUuid();
    }

}
