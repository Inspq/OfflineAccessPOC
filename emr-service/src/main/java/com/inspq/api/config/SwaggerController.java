package com.inspq.emr.config;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SwaggerController {

    @RequestMapping("/")
    public String getSwagger(Model model) {
        return "redirect:/swagger-ui.html";
    }
}
