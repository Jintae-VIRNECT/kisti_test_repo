package com.virnect.login.route;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-Login
 * @email practice1356@gmail.com
 * @description Vue Route Controller
 * @since 2020.03.05
 */
@Slf4j
@CrossOrigin
@Controller
public class RouteController {

    @RequestMapping("/")
    public String login() {
        return "index";
    }

    @GetMapping("/find/email")
    public String userFindEmailRedirect() {
        return "index";
    }

    @GetMapping("/find/reset_password")
    public String userFindPasswordRedirect() {
        return "index";
    }

    @RequestMapping(value = "{_:^(?!api).*$}")
    public String redirectApi(HttpServletRequest request) {
        log.info("URL entered directly into the Browser, so we need to redirect...");
        log.info("[REQUEST] => {}", request.getRequestURI());
        return "forward:/";
    }
}
