package com.example.rces.mapper;

import com.example.rces.dto.InspectionViolationCreateDTO;
import com.example.rces.dto.InspectionViolationDTO;
import com.example.rces.models.InspectionViolation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {
                SubDivisionMapper.class,
                EmployeeMapper.class
        })
public interface InspectionViolationMapper extends BaseMapper<InspectionViolation, InspectionViolationDTO, InspectionViolationCreateDTO> {

        @Override
        @Mapping(target = "subDivision", ignore = true)
        InspectionViolation toEntityFromCreateDTO(InspectionViolationCreateDTO createDto);

        @Override
        @Mapping(target = "inspectionId", source = "inspection.id")
        InspectionViolationDTO toDTO(InspectionViolation entity);
}
