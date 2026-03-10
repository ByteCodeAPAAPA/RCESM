package com.example.rces.spm.services.service;

import com.example.rces.spm.models.SPMCustomerOrder;
import com.example.rces.spm.services.SPMRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
public class CustomerOrderService {
    private final SPMRepository spmRepository;

    public CustomerOrderService(SPMRepository spmRepository) {
        this.spmRepository = spmRepository;
    }

    public Map<List<SPMCustomerOrder>, List<SPMCustomerOrder>> getFirst10BurningCustomerOrderAndAll() {
        List<SPMCustomerOrder> allOrders = spmRepository.findAll(SPMCustomerOrder.class);
        List<SPMCustomerOrder> burningOrders = allOrders.stream()
                .filter(co -> co.getDateDue() != null)
                .sorted(Comparator.comparingLong(co -> Math.abs(ChronoUnit.DAYS.between(co.getDateDue(), LocalDateTime.now()))))
                .limit(10)
                .toList();
        return Map.of(burningOrders, allOrders);
    }

    //Не работает из за СКУЭЛЯ ЕБ__ОГО
//    public Map<PrimaryDemand, JobComponent> getPageMainJobComponentForPrimaryDemand(Long customerOrderId, int page, int size) {
//        List<Object[]> result = spmRepository.getEntityManager().createQuery(
//                        """
//                                SELECT pd, jc
//                                FROM PrimaryDemand pd
//                                JOIN FETCH pd.item
//                                JOIN JobComponent jc ON pd.id = jc.primarydemand.id AND pd.item.id = jc.item.id AND jc.bomLevel = 1
//                                JOIN FETCH jc.item
//                                LEFT JOIN FETCH jc.jobSteps
//                                WHERE pd.customerorder.id = :id
//                                ORDER BY pd.demandType, pd.stormSingleString
//                                """, Object[].class)
//                .setParameter("id", customerOrderId)
//                .setFirstResult((page - 1) * size)
//                .setMaxResults(size)
//                .getResultList();
//
//        return result.stream()
//                .collect(Collectors.toMap(row -> (PrimaryDemand) row[0],
//                        row -> (JobComponent) row[1],
//                        (existing, replacement) -> replacement,
//                        LinkedHashMap::new
//                ));
//    }


}
