package com.glomozda.cloudnativeapp1.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(path="/")
public class HomeController {
    @GetMapping("")
    public ResponseEntity<String> healthcheck() {
        return (new ResponseEntity<>("Health Check Passed", null, HttpStatus.OK));
    }
}
