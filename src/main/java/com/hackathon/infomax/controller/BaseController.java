package com.hackathon.infomax.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/base")
public class BaseController {

    @GetMapping("/login")
    public String login(Model model) {
        return "index";
    }

    @GetMapping("/listResult")
    public String listResult() {
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
