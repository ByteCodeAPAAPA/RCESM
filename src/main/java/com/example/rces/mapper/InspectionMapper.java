package com.example.rces.mapper;

import com.example.rces.dto.InspectionCreateDTO;
import com.example.rces.dto.InspectionDTO;
import com.example.rces.models.Inspection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {
                EmployeeMapper.class,
                SubDivisionMapper.class
        })
public interface InspectionMapper extends BaseMapper<Inspection, InspectionDTO, InspectionCreateDTO> {

    @Override
    @Mapping(target = "subDivision", ignore = true)
    Inspection toEntityFromCreateDTO(InspectionCreateDTO createDto);

    @Override
    @Mapping(target = "primaryInspectionId", source = "primaryInspection.id")
    @Mapping(target = "violation", ignore = true)
    InspectionDTO toDTO(Inspection entity);
}
