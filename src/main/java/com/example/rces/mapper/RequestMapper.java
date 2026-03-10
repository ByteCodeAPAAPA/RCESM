package com.example.rces.mapper;

import com.example.rces.dto.CreateRequestDto;
import com.example.rces.dto.RequestDto;
import com.example.rces.models.CustomerOrder;
import com.example.rces.models.Employee;
import com.example.rces.models.Requests;
import com.example.rces.models.SubDivision;
import com.example.rces.models.enums.GeneralReason;
import com.example.rces.models.enums.Item;
import com.example.rces.models.enums.Status;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RequestMapper extends BaseMapper<Requests, RequestDto, CreateRequestDto> {

    @Mapping(target = "typeRequest", source = "type")
    @Mapping(target = "qty", source = "qty")
    @Mapping(target = "comment", source = "comment")
    @Mapping(target = "control", source = "control")
    @Mapping(target = "requestNumber", source = "requestNumber")
    @Mapping(target = "title", source = "titleJson")
    @Mapping(target = "reason_wr", source = "reasonsJson")
    Requests toRequestFromCreateDTO(CreateRequestDto createDto);

    default Requests createFullRequest(CreateRequestDto createDto,
                                       Item item,
                                       GeneralReason reason,
                                       SubDivision mlmNode,
                                       Employee employee,
                                       CustomerOrder customerOrder,
                                       Employee createdEmployee) {
        Requests requests = toRequestFromCreateDTO(createDto);
        setAdditionalFields(requests, item, reason, mlmNode, employee, customerOrder, createdEmployee);
        return requests;
    }

    default void setAdditionalFields(Requests requests,
                                     Item item,
                                     GeneralReason reason,
                                     SubDivision mlmNode,
                                     Employee employee,
                                     CustomerOrder customerOrder,
                                     Employee createdEmployee) {
        requests.setItem(item);
        requests.setReason(reason);
        requests.setSubDivision(mlmNode);
        if (employee != null) {
            requests.setEmployee(employee);
        }
        requests.setCustomerOrder(customerOrder);
        requests.setCreatedBy(createdEmployee);
        requests.setStatus(Status.New);
    }

    @Override
    @Mapping(target = "type", source = "typeRequest")
    @Mapping(target = "reasonsJson", source = "reason_wr")
    RequestDto toDTO(Requests entity);

}


