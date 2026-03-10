package com.example.rces.mapper;

import com.example.rces.dto.MachineCreateDto;
import com.example.rces.dto.MachineDto;
import com.example.rces.models.Machine;
import org.mapstruct.*;

import java.util.List;

@Mapper(
    componentModel = "spring",
    uses = {EmployeeMapper.class, ImagesMapper.class},
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface MachineMapper {

    List<MachineDto> toDTOList(List<Machine> machines);

    MachineDto toDto(Machine machine);

    Machine toEntityFromCreateDto(MachineCreateDto createDto);

    @Mapping(target = "admittedEmployees", source = "admittedEmployeesList")
    @Mapping(target = "responsibleEmployees", source = "responsibleEmployeesList")
    Machine updateEntity(@MappingTarget Machine machine, MachineDto dto);

}
