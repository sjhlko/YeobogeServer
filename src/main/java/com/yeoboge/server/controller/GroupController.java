package com.yeoboge.server.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GroupController {

    @GetMapping("/shake")
    public String getAndroidShake() {
        return "💘 Group Recommendation List 💘";
    }
}
