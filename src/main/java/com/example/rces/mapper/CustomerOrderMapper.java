package com.example.rces.mapper;

import com.example.rces.dto.CustomerOrderDTO;
import com.example.rces.models.CustomerOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerOrderMapper extends BaseMapper<CustomerOrder, CustomerOrderDTO, CustomerOrder> {

    @Mapping(target = "employeeName", ignore = true)
    CustomerOrderDTO toDTO(CustomerOrder customerOrder);
}
