package com.example.rces.mapper;

import com.example.rces.dto.ReasonDto;
import com.example.rces.models.Reason;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReasonMapper {

    @Mapping(target = "id", ignore = true)
    Reason toEntity(String text, String type);

    List<ReasonDto> toDtoList(List<Reason> entities);
}
