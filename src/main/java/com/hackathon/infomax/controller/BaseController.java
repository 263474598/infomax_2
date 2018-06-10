package com.hackathon.infomax.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/base")
public class BaseController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/listResult/{targetDate}")
    public String listResult(@PathVariable("targetDate") String targetDate, Model model) {

        model.addAttribute("targetDate", targetDate);

        return "listResult";
    }

    @GetMapping("/showDiff")
    public String showDiff() {
        return "showDiff";
    }

    @GetMapping("/temp")
    public String temp() {
        return "temp";
    }

    @GetMapping("/upload")
    public String upload() {
        return "upload";
    }

}
