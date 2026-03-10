package com.example.rces.mapper;

import com.example.rces.dto.DocumentFileDTO;
import com.example.rces.models.DocumentFile;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DocumentFileMapper {

    DocumentFileDTO toDTO(DocumentFile entity);

}
