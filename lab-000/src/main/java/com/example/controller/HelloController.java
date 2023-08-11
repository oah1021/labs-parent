package com.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author JUNHAO
 * @date 2023/3/25
 * @Description Hello
 */
@RestController
@RequestMapping("/hello")
public class HelloController {
    @GetMapping("/test")
    public String hello() {
        return "Hello-lab-000";
    }
}
