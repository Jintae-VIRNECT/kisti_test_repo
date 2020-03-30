package com.virnect.login.route;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-Login
 * @email practice1356@gmail.com
 * @description Vue Route Controller
 * @since 2020.03.05
 */
@CrossOrigin
@Controller
public class RouteController {

    @RequestMapping("/")
    public String login() {
        return "index";
    }

    @RequestMapping("/{path:(?!api).*}")
    public String register() {
        return "forward:/";
    }
}