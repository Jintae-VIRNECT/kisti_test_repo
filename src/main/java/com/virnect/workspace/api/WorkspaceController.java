package com.virnect.workspace.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * Project: PF-Workspace
 * DATE: 2020-01-08
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
@Slf4j
@RestController
public class WorkspaceController {
    @GetMapping("/{message}")
    public String helloWorld(@PathVariable("message") final String message) {
        return "[" + LocalDateTime.now() + "] [" + message + "]";
    }
}
