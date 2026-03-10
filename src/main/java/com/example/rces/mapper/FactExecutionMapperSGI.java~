package com.example.rces.mapper;

import com.example.rces.dto.FactExecutionSGIDTO;
import com.example.rces.models.FactExecutionSGI;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {
                ImagesMapper.class
        })
public interface FactExecutionMapperSGI extends BaseMapper<FactExecutionSGI, FactExecutionSGIDTO, FactExecutionSGIDTO> {
}
