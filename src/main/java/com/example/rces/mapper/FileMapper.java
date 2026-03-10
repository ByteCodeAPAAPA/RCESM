package com.example.rces.mapper;

import com.example.rces.dto.FileDTO;
import com.example.rces.models.DocumentFile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FileMapper extends BaseMapper<DocumentFile, FileDTO, FileDTO> {

    @Override
    @Mapping(target = "name", source = "baseFileName")
    @Mapping(target = "data", source = "content")
    FileDTO toDTO(DocumentFile entity);

}
