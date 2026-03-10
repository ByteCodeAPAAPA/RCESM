package com.example.rces.mapper;

import com.example.rces.dto.DocumentCreateDTO;
import com.example.rces.dto.DocumentDTO;
import com.example.rces.models.Document;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {
                DocumentFileMapper.class
        })
public interface DocumentMapper extends BaseMapper<Document, DocumentDTO, DocumentCreateDTO> {

        @Override
        @Mapping(target = "files", ignore = true)
        DocumentDTO toDTO(Document entity);
}
