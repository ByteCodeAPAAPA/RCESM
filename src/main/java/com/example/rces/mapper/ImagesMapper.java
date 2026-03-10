package com.example.rces.mapper;

import com.example.rces.dto.ImagesDTO;
import com.example.rces.models.Images;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.Base64;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ImagesMapper implements BaseMapper<Images, ImagesDTO, ImagesDTO> {

    @Override
    @Mapping(target = "data", source = "base64Data")
    @Mapping(target = "mainlink", source = "mainlinkId")
    public abstract ImagesDTO toDTO(Images image);

    @Override
    @Mapping(target = "data", source = "data", qualifiedByName = "stringToByteArray")
    public abstract Images toEntity(ImagesDTO dto);

    @Override
    @Mapping(target = "data", source = "data", qualifiedByName = "stringToByteArray")
    public abstract Images toEntityFromCreateDTO(ImagesDTO createDto);

    @Named("stringToByteArray")
    static byte[] stringToByteArray(String base64Image) {
        if (base64Image == null || base64Image.isEmpty()) {
            return null;
        }

        if (!base64Image.startsWith("data:")) {
            throw new IllegalArgumentException("Некорректный формат изображения");
        }

        int commaIndex = base64Image.indexOf(',');
        if (commaIndex == -1) {
            throw new IllegalArgumentException("Некорректный формат изображения");
        }

        String base64Data = base64Image.substring(commaIndex + 1);
        return Base64.getDecoder().decode(base64Data);
    }


}