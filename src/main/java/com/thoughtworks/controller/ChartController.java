package com.thoughtworks.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Created by pyang on 15/03/2017.
 */
@Controller
public class ChartController {

    @GetMapping(value = "/chart/show")
    public String showChart() {
        return "showchart";
    }
}
