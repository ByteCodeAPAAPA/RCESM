package com.example.rces.spm.services.service;

import com.example.rces.spm.models.*;
import com.example.rces.spm.services.SPMRepository;
import jakarta.ws.rs.core.Link;
import org.springframework.context.ApplicationContextException;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PrimaryDemandService {
    private final SPMRepository repository;

    public PrimaryDemandService(SPMRepository repository) {
        this.repository = repository;
    }

    public JobComponent getMainJobComponentForPrimaryDemand(PrimaryDemand primaryDemand) {
        List<JobComponent> results = repository.getEntityManager()
                .createQuery("""
                    SELECT jc
                    FROM JobComponent jc
                    WHERE jc.item.id = :itemId
                    AND jc.primarydemand.id = :pdId
                    AND jc.bomLevel = 1
                    """, JobComponent.class)
                .setParameter("itemId", primaryDemand.getItem().getId())
                .setParameter("pdId", primaryDemand.getId())
                .getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    public Map<PrimaryDemand, JobComponent> getMainJobComponentForPrimaryDemand(List<PrimaryDemand> primaryDemands) {
        List<JobComponent> jobComponentList = repository.getEntityManager()
                .createQuery("""
                        SELECT jc from JobComponent jc
                        JOIN FETCH jc.item
                        WHERE jc.primarydemand.id in :pdIds
                        AND jc.item.id in :itemIds
                        AND jc.bomLevel = 1
                        """, JobComponent.class)
                .setParameter("pdIds", primaryDemands.stream().map(PrimaryDemand::getId).toList())
                .setParameter("itemIds", primaryDemands.stream().map(PrimaryDemand::getItem).map(Item::getId).toList())
                .getResultList();
        Map<Long, PrimaryDemand> pdById = primaryDemands
                .stream()
                .sorted(
                        Comparator.comparing(PrimaryDemand::getDemandType)
                                .thenComparing(PrimaryDemand::getStormSingleString)
                )
                .collect(Collectors.toMap(
                        PrimaryDemand::getId,
                        Function.identity(),
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ));
        return jobComponentList.stream()
                .filter(jc -> pdById.containsKey(jc.getPrimarydemand().getId()))
                .collect(Collectors.toMap(
                        jc -> pdById.get(jc.getPrimarydemand().getId()),
                        Function.identity(),
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ));
    }
}
