package com.example.rces.service;

import com.example.rces.dto.MachineCreateDto;
import com.example.rces.dto.MachineDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MachineService {

    List<MachineDto> getAllMachine();

    MachineDto findByNumber(Integer number);

    MachineDto createMachine(MachineCreateDto createDto);

    MachineDto updateMachine(MachineDto machineDto);

    void deleteMachineByNumber(Integer number);

    void addPdfToMachine(Integer machineNumber, MultipartFile[] files);

}
