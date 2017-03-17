package com.thoughtworks.dao;

import com.thoughtworks.model.Computer;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by pyang on 15/03/2017.
 */
public interface ComputerRepository extends JpaRepository<Computer, Integer>{
}
