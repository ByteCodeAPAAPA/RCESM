package com.example.rces.mapper;

import com.example.rces.dto.FactExecutionSGIDTO;
import com.example.rces.dto.SgiCreateDTO;
import com.example.rces.dto.SgiDTO;
import com.example.rces.dto.SubSgiDTO;
import com.example.rces.models.FactExecutionSGI;
import com.example.rces.models.SGI;
import com.example.rces.models.enums.Color;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.rces.utils.DateUtil.formatedDate;
import static com.example.rces.utils.DateUtil.parseLocalDate;

@Component
public class SgiMapper implements BaseMapper<SGI, SgiDTO, SgiCreateDTO> {

    private final EmployeeMapper employeeMapper;

    @Autowired
    protected SgiMapper(EmployeeMapper employeeMapper) {
        this.employeeMapper = employeeMapper;
    }

    @Override
    public SGI toEntityFromCreateDTO(SgiCreateDTO dto) {
        SGI sgi = new SGI();
        sgi.setWorkShop(dto.getWorkcenter());
        sgi.setEvent(dto.getEvent());
        sgi.setActions(dto.getActions());
        sgi.setDepartment(SGI.Department.valueOf(dto.getDepartment()));
        sgi.setNote(dto.getNote());
        sgi.setDesiredDate(dto.getDesiredDate());
        sgi.setEmployee(employeeMapper.toEntity(dto.getEmployee()));
        sgi.setComment("");
        return sgi;
    }

    @Override
    public SgiDTO toDTO(SGI sgi) {
        SgiDTO dto = new SgiDTO();
        dto.setId(sgi.getId());
        dto.setNumber(String.valueOf(sgi.getRequestNumber()));
        dto.setColor(sgi.getColor().name());
        dto.setWorkcenter(sgi.getWorkShop());
        dto.setEvent(sgi.getEvent());
        dto.setActions(sgi.getActions());
        dto.setDepartment(sgi.getDepartment().name());
        dto.setDepartmentName(sgi.getDepartment().getName());
        dto.setEmployee(employeeMapper.toDTO(sgi.getEmployee()));
        dto.setDesiredDate(sgi.getDesiredDate());
        dto.setPlanDate(sgi.getPlanDate());
        dto.setNote(sgi.getNote());
        dto.setComment(Optional.ofNullable(sgi.getComment()).orElse(""));
        dto.setAgree(sgi.getAgreed());
        dto.setSubSGI(subSgiToDto(sgi.getSubSGI()));
        dto.setFactExecution(factExecutionToDto(sgi.getExecution()));
        dto.setDocumentId(sgi.getDocument()!=null ? sgi.getDocument().getId() : null);
        dto.setParent(sgi.getParentSGI()!=null ? sgi.getParentSGI().getId() : null);

        return dto;
    }

    @Override
    public SGI toEntity(SgiDTO sgiDTO) {
        SGI sgi = new SGI();
        sgi.setId(sgiDTO.getId());
        sgi.setRequestNumber(Integer.parseInt(sgiDTO.getNumber()));
        sgi.setColor(Color.valueOf(sgiDTO.getColor()));
        sgi.setWorkShop(sgiDTO.getWorkcenter());
        sgi.setEvent(sgiDTO.getEvent());
        sgi.setActions(sgiDTO.getActions());
        sgi.setDepartment(SGI.Department.valueOf(sgiDTO.getDepartment()));
        sgi.setNote(sgiDTO.getNote());
        sgi.setDesiredDate(sgiDTO.getDesiredDate());
        sgi.setPlanDate(sgiDTO.getPlanDate());
        sgi.setEmployee(employeeMapper.toEntity(sgiDTO.getEmployee()));
        sgi.setComment(sgiDTO.getComment());
        sgi.setAgreed(sgiDTO.getAgree());
        sgi.setSubSGI(subSgiDtoToEntity(sgiDTO.getSubSGI()));
        sgi.setExecution(factExecutionDtoToEntity(sgiDTO.getFactExecution()));
        return sgi;
    }


    private List<SubSgiDTO> subSgiToDto(List<SGI> subs) {
        if (subs == null) {
            return new ArrayList<>();
        }
        List<SubSgiDTO> subList = new ArrayList<>();
        for (SGI sub : subs) {
            SubSgiDTO dto = new SubSgiDTO();
            dto.setId(sub.getId());
            dto.setNumber(String.valueOf(sub.getRequestNumber()));
            dto.setWorkcenter(sub.getWorkShop());
            dto.setEvent(sub.getEvent());
            dto.setActions(sub.getActions());
            dto.setDepartment(sub.getDepartment().name());
            dto.setNote(sub.getNote());
            dto.setDesiredDate(sub.getDesiredDate());
            dto.setAgree(sub.getAgreed());
            dto.setEmployee(employeeMapper.toDTO(sub.getEmployee()));
            dto.setColor(sub.getColor().name());
            dto.setDepartmentName(sub.getDepartment().getName());
            dto.setPlanDate(sub.getPlanDate());
            dto.setComment(Optional.ofNullable(sub.getComment()).orElse(""));
            dto.setFactExecution(factExecutionToDto(sub.getExecution()));
            dto.setParent(sub.getParentSGI()!=null ? sub.getParentSGI().getId() : null);
            dto.setDocumentId(sub.getDocument()!=null ? sub.getDocument().getId() : null);
            subList.add(dto);
        }
        return subList;
    }

    private List<SGI> subSgiDtoToEntity(List<SubSgiDTO> subs) {
        List<SGI> subList = new ArrayList<>();
        for (SubSgiDTO sub : subs) {
            SGI sgi = new SGI();
            sgi.setId(sub.getId());
            sgi.setRequestNumber(Integer.parseInt(sub.getNumber()));
            sgi.setColor(Color.valueOf(sub.getColor()));
            sgi.setWorkShop(sub.getWorkcenter());
            sgi.setEvent(sub.getEvent());
            sgi.setActions(sub.getActions());
            sgi.setDepartment(SGI.Department.valueOf(sub.getDepartment()));
            sgi.setNote(sub.getNote());
            sgi.setDesiredDate(sub.getDesiredDate());
            sgi.setPlanDate(sub.getPlanDate());
            sgi.setEmployee(employeeMapper.toEntity(sub.getEmployee()));
            sgi.setComment(sub.getComment());
            sgi.setAgreed(sub.getAgree());
            sgi.setExecution(factExecutionDtoToEntity(sub.getFactExecution()));
            subList.add(sgi);
        }
        return subList;
    }

    private FactExecutionSGIDTO factExecutionToDto(FactExecutionSGI factExecutionSGI) {
        FactExecutionSGIDTO dto = new FactExecutionSGIDTO();
        dto.setId(factExecutionSGI.getId());
        dto.setExecutionDate(formatedDate(factExecutionSGI.getExecutionDate()));
        dto.setReport(factExecutionSGI.getReport());
        return dto;
    }

    private FactExecutionSGI factExecutionDtoToEntity(FactExecutionSGIDTO dto) {
        FactExecutionSGI factExecutionSGI = new FactExecutionSGI();
        factExecutionSGI.setId(dto.getId());
        factExecutionSGI.setExecutionDate(parseLocalDate(dto.getExecutionDate()));
        factExecutionSGI.setReport(dto.getReport());
        return factExecutionSGI;
    }

}
