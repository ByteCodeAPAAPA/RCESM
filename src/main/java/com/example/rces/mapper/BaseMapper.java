package com.example.rces.mapper;

import org.springframework.data.domain.Page;

import java.util.List;

public interface BaseMapper<T, D, C> {


    T toEntity(D dto);

    T toEntityFromCreateDTO(C createDto);

    D toDTO(T entity);

    default Page<D> toDTOPage(Page<T> entities) {
        return entities.map(this::toDTO);
    }

    default List<D> toDTOList(List<T> entities) {
        return entities.stream().map(this::toDTO).toList();
    }
}
