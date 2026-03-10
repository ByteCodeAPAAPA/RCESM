package com.example.rces.mapper;

import com.example.rces.dto.NTDocumentCreateDTO;
import com.example.rces.dto.NTDocumentDTO;
import com.example.rces.dto.NTDocumentReferenceDTO;
import com.example.rces.models.NTDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NTDocumentsMapper extends BaseMapper<NTDocument, NTDocumentDTO, NTDocumentCreateDTO> {

    @Override
    NTDocument toEntityFromCreateDTO(NTDocumentCreateDTO dto);

    @Override
    @Mapping(target = "references", ignore = true)
    NTDocument toEntity(NTDocumentDTO dto);

    @Override
    @Mapping(target = "documentId", source = "document.id")
    @Mapping(target = "references", source = "references", qualifiedByName = "mapReferencesToIds")
    NTDocumentDTO toDTO(NTDocument entity);

    @Mapping(target = "documentId", source = "document.id")
    NTDocumentReferenceDTO toReferenceDTO(NTDocument entity);

    @Named("mapReferencesToIds")
    default List<UUID> mapReferencesToIds(List<NTDocument> references) {
        if (references == null) {
            return new ArrayList<>();
        }
        return references.stream()
                .map(NTDocument::getId)
                .collect(Collectors.toList());
    }

}
