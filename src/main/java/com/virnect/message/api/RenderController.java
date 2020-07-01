package com.virnect.message.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Project: PF-Message
 * DATE: 2020-07-01
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j(topic = "MainController")
@Controller
@RequestMapping(value = "/")
public class RenderController {

    @GetMapping("/main")
    public String redirect(Model model) {
        return "main";

    }
}
