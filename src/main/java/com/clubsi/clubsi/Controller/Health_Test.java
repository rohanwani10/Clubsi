package com.clubsi.clubsi.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("")
public class Health_Test {

    @GetMapping("/test")
    public String getMethodName() {
        return "Health Check: Test Successful";
    }

}
