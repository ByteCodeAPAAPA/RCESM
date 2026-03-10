package com.example.rces.service;

import com.example.rces.dto.SubDivisionDTO;
import com.example.rces.models.SubDivision;
import lombok.extern.java.Log;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

import java.util.List;

public interface SubDivisionService {

    SubDivision getByName(String name);

    SubDivision getByCode(String code);

    SubDivisionDTO getDTOByName(String name);

    List<SubDivisionDTO> getAll();

}
