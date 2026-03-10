package com.example.rces.mapper;

import com.example.rces.dto.EmployeeCreateDTO;
import com.example.rces.dto.EmployeeDTO;
import com.example.rces.models.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = SubDivisionMapper.class)
public interface EmployeeMapper extends BaseMapper<Employee, EmployeeDTO, EmployeeCreateDTO> {

}
