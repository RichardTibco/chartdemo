package com.thoughtworks.controller;

import com.thoughtworks.dao.ComputerRepository;
import com.thoughtworks.model.Computer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by pyang on 15/03/2017.
 */
@RestController
public class AjaxController {

    @Autowired
    private ComputerRepository computerRepository;

    @RequestMapping("/data")
    public List<Computer> getData(){
        return computerRepository.findAll();
    }
}
