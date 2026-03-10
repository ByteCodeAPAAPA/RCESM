package com.example.rces.mapper;

import com.example.rces.dto.InconsistencyCreateDto;
import com.example.rces.dto.InconsistencyDto;
import com.example.rces.models.Inconsistency;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InconsistenciesMapper extends BaseMapper<Inconsistency, InconsistencyDto, InconsistencyCreateDto>{

    InconsistencyDto toDTO(Inconsistency entity);

    Inconsistency toEntityFromCreateDTO(InconsistencyCreateDto entity);

}
