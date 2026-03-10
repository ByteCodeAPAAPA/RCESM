package com.example.rces.service.impl;

import com.example.rces.dto.EmployeeDTO;
import com.example.rces.dto.MachineCreateDto;
import com.example.rces.dto.MachineDto;
import com.example.rces.mapper.DocumentFileMapper;
import com.example.rces.mapper.EmployeeMapper;
import com.example.rces.mapper.ImagesMapper;
import com.example.rces.mapper.MachineMapper;
import com.example.rces.models.Document;
import com.example.rces.models.DocumentFile;
import com.example.rces.models.Images;
import com.example.rces.models.Machine;
import com.example.rces.repository.MachineRepository;
import com.example.rces.service.EmployeeService;
import com.example.rces.service.MachineService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static com.example.rces.utils.FilesUtil.determineFileType;

@Service
public class MachineServiceImpl implements MachineService {

    private final MachineRepository machineRepository;
    private final MachineMapper machineMapper;
    private final EmployeeService employeeService;
    private final EmployeeMapper employeeMapper;
    private final ImagesMapper imagesMapper;
    private final DocumentFileMapper documentFileMapper;


    @Autowired
    public MachineServiceImpl(MachineRepository machineRepository, MachineMapper machineMapper, EmployeeService employeeService, EmployeeMapper employeeMapper, ImagesMapper imagesMapper, DocumentFileMapper documentFileMapper) {
        this.machineRepository = machineRepository;
        this.machineMapper = machineMapper;
        this.employeeService = employeeService;
        this.employeeMapper = employeeMapper;
        this.imagesMapper = imagesMapper;
        this.documentFileMapper = documentFileMapper;
    }

    @Override
    public List<MachineDto> getAllMachine() {
        return machineMapper.toDTOList(machineRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public MachineDto findByNumber(Integer number) {

        Machine machine = machineRepository.findByNumber(number);

        if (machine == null) {
            throw new EntityNotFoundException("Machine not found with number: " + number);
        }

        MachineDto dto = machineMapper.toDto(machine);

        dto.setAdmittedEmployeesList(machine.getAdmittedEmployees().stream()
                .map(employeeMapper::toDTO)
                .toList());

        dto.setResponsibleEmployeesList(machine.getResponsibleEmployees().stream()
                .map(employeeMapper::toDTO)
                .toList());

        dto.setImageUrls(machine.getDocument().getImages()
                .stream()
                .filter(Objects::nonNull)
                .map(imagesMapper::toDTO)
                .toList());

        dto.setPdfs(machine.getDocument().getFiles().stream()
                .filter(Objects::nonNull)
                .map(documentFileMapper::toDTO)
                .toList());

        return dto;
    }

    @Override
    @Transactional
    public MachineDto createMachine(MachineCreateDto dto) {
        Machine machine = machineMapper.toEntityFromCreateDto(dto);

        Document document = new Document();
        document.setName("Документация для станка № " + dto.getNumber());

        if (dto.getDocumentFiles() != null && dto.getDocumentFiles().length > 0 && !dto.getDocumentFiles()[0].isEmpty()) {
            for (MultipartFile file : dto.getDocumentFiles()) {
                try {

                    DocumentFile documentFile = new DocumentFile();
                    documentFile.setContent(file.getBytes());
                    documentFile.setBaseFileName(file.getOriginalFilename());

                    documentFile.setDocument(document);

                    document.getFiles().add(documentFile);
                } catch (Exception e) {
                    throw new RuntimeException("Document file not found");
                }
            }
        }

        if (dto.getAdmittedEmployeesList() != null) {
            dto.getAdmittedEmployeesList().stream()
                    .filter(Objects::nonNull)
                    .map(employeeService::loadUserByUsername)
                    .forEach(machine::addAdmittedEmployees);
        }

        if (dto.getResponsibleEmployeesList() != null) {
            dto.getResponsibleEmployeesList().stream()
                    .filter(Objects::nonNull)
                    .map(employeeService::loadUserByUsername)
                    .forEach(machine::addResponsibleEmployees);
        }

        if (dto.getAdditionalFiles() != null && dto.getAdditionalFiles().length > 0 && !dto.getAdditionalFiles()[0].isEmpty()) {
            for (MultipartFile imageFile : dto.getAdditionalFiles()) {
                try {
                    Images newImage = new Images();
                    newImage.setData(imageFile.getBytes());
                    newImage.setName(imageFile.getOriginalFilename());

                    newImage.setDocument(document);

                    document.getImages().add(newImage);
                } catch (IOException e) {
                    throw new RuntimeException("Не удалось обработать файл изображения", e);
                }
            }
        }
        machine.setDocument(document);

        Machine savedMachine = machineRepository.save(machine);

        return machineMapper.toDto(savedMachine);
    }

    @Override
    @Transactional
    public MachineDto updateMachine(MachineDto dto) {
        Machine machine = machineRepository.findByNumber(dto.getNumber());

        List<EmployeeDTO> employeeDto = dto.getAdmittedEmployeesList().stream()
                .map(employeeDTO -> employeeMapper.toDTO(employeeService.loadUserByUsername(employeeDTO.getName())))
                .toList();

        dto.setAdmittedEmployeesList(employeeDto);

        employeeDto = dto.getResponsibleEmployeesList().stream()
                .map(employeeDTO -> employeeMapper.toDTO(employeeService.loadUserByUsername(employeeDTO.getName())))
                .toList();

        dto.setResponsibleEmployeesList(employeeDto);

        if (machine == null) {
            throw new EntityNotFoundException("Machine not found with number: " + dto.getNumber());
        }

        Machine updatedMachine = machineMapper.updateEntity(machine, dto);

        return machineMapper.toDto(machineRepository.save(updatedMachine));
    }

    @Override
    @Transactional
    public void deleteMachineByNumber(Integer number) {

        Machine machine = machineRepository.findByNumber(number);

        if (machine != null) {
            machineRepository.delete(machine);
        }
    }

    @Override
    @Transactional
    public void addPdfToMachine(Integer machineNumber, MultipartFile[] files) {

        Machine machine = machineRepository.findByNumber(machineNumber);

        if (machine == null) {
            throw new EntityNotFoundException("Machine not found with number: " + machineNumber);
        }

        Document document = machine.getDocument();

        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;
            try {
                DocumentFile documentFile = new DocumentFile();
                documentFile.setBaseFileName(file.getOriginalFilename());
                documentFile.setContent(file.getBytes());
                documentFile.setDocument(document);
                documentFile.setType(determineFileType(file.getOriginalFilename()));

                document.getFiles().add(documentFile);
            } catch (IOException e) {
                throw new RuntimeException("Document file not found");
            }
        }

        machineRepository.save(machine);
    }
}
