package com.example.rces.mapper;

import com.example.rces.dto.CustomerOrderDTO;
import com.example.rces.models.CustomerOrder;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerOrderMapper extends BaseMapper<CustomerOrder, CustomerOrderDTO, CustomerOrder> {

    CustomerOrderDTO toDTO(CustomerOrder customerOrder);
}
