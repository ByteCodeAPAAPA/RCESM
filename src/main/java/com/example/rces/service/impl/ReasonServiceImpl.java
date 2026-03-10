package com.example.rces.service.impl;

import com.example.rces.dto.ReasonDto;
import com.example.rces.mapper.ReasonMapper;
import com.example.rces.models.Reason;
import com.example.rces.repository.ReasonRepository;
import com.example.rces.service.ReasonService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReasonServiceImpl implements ReasonService {

    private final ReasonRepository reasonRepository;
    private final ReasonMapper reasonMapper;

    public ReasonServiceImpl(ReasonRepository reasonRepository, ReasonMapper reasonMapper) {
        this.reasonRepository = reasonRepository;
        this.reasonMapper = reasonMapper;
    }


    @Override
    public void createOrUpdateReason(String reasonText, String requestType) {
        Reason reason = reasonRepository.findByText(reasonText);
        if (reason == null) {
            reasonRepository.save(reasonMapper.toEntity(reasonText, requestType));
        }
    }

    @Override
    public List<ReasonDto> getAllReasons() {
        return reasonMapper.toDtoList(reasonRepository.findAll());
    }

    @Override
    public List<ReasonDto> searchReasons(String search) {
        String cleanedSearch = search.trim();
        String searchTerm = "%" + cleanedSearch + "%";
        List<Reason> reasons = reasonRepository.searchByText(searchTerm);
        return reasonMapper.toDtoList(reasons);
    }

}
