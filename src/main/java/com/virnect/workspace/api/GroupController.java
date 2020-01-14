package com.virnect.workspace.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Project: PF-Workspace
 * DATE: 2020-01-08
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION: group service rest api controller
 */
@Slf4j
@RestController
@RequestMapping("/groups")
public class GroupController {
    @PostMapping
    public ResponseEntity createGroup(){
        return ResponseEntity.ok(200);
    }/*
    @GetMapping
    public ResponseEntity getGroupPermission(){
        return ResponseEntity.ok(200);
    }
    @PostMapping
    public ResponseEntity setGroupPermission(){
        return ResponseEntity.ok(200);
    }
    @GetMapping
    public ResponseEntity getGroups(){
        return ResponseEntity.ok(200);
    }
    @DeleteMapping
    public ResponseEntity deleteGroups(){
        return ResponseEntity.ok(200);
    }
    @GetMapping
    public ResponseEntity leaveGroups(){
        return ResponseEntity.ok(200);
    }
    @GetMapping
    public ResponseEntity addGroups(){
        return ResponseEntity.ok(200);
    }*/
}
