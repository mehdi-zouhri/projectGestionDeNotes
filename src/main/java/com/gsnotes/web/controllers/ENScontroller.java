package com.gsnotes.web.controllers;

import com.gsnotes.bo.Module;
import com.gsnotes.bo.*;
import com.gsnotes.services.IEnseignantService;
import com.gsnotes.utils.ExcelImporter;
import com.gsnotes.web.models.UserAndAccountInfos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Controller
@RequestMapping("/prof")
public class ENScontroller {

    @Autowired
    ServletContext context;

    @Autowired
    private IEnseignantService sEnseignantService;

    @Autowired
    private HttpSession httpSession;








    @PostMapping("/import")
    public String upload(@RequestParam("file") MultipartFile file, Model model) {
        String filename = context.getRealPath("uploads") + file.getOriginalFilename();

        sEnseignantService.save(file, filename);

        try {
            sEnseignantService.processExcelFile(filename, false);
        }
        catch (FileAlreadyExistsException ex) {
            model.addAttribute("confirm", true);
            model.addAttribute("filename", filename);
            return "/prof/userHomePage";
        }
        catch (Exception ex) {
            model.addAttribute("message", filename + " uploaded failed: " + ex.getMessage());
            model.addAttribute("status", false);
            return "/prof/userHomePage";
        }
        model.addAttribute("message", file.getOriginalFilename() + " uploaded successfuly");
        model.addAttribute("status", true);

        return "/prof/userHomePage";
    }

    @RequestMapping("/confirm")
    public String confirm(@RequestParam("filename") String filename, Model model) {
        System.out.println(filename);

        sEnseignantService.clear();
        try {
            sEnseignantService.processExcelFile(filename, true);
        }
        catch (FileAlreadyExistsException ex) {
            model.addAttribute("confirm", true);
            model.addAttribute("filename", filename);
            return "/prof/userHomePage";
        }
        catch (Exception ex) {
            model.addAttribute("message", filename + " uploaded failed: " + ex.getMessage());
            model.addAttribute("status", false);
            return "/prof/userHomePage";
        }
        model.addAttribute("message", filename + " uploaded successfuly");
        model.addAttribute("status", true);

        return "/prof/userHomePage";
    }





















}
