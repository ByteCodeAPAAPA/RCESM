package com.example.rces.spm.services.service;

import com.example.rces.spm.models.Item;
import com.example.rces.spm.models.RouteRevision;
import com.example.rces.spm.services.SPMRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class ItemService {
    private final SPMRepository repository;

    public ItemService(SPMRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public BigDecimal getTotalProductionForItem(Item item, BigDecimal qty) {
        return getTotalProductionTimeForItem(repository.getEntityManager(), item, qty).setScale(3, RoundingMode.DOWN);
    }

    private BigDecimal getTotalProductionTimeForItem(EntityManager em, Item item, BigDecimal qty) {
        RouteRevision routeRevision = em
                .createQuery("""
                        select rr from Route r
                        left join RouteRevision rr on rr.route = r
                        where r.item = :item
                        and r.isPrimary
                        """, RouteRevision.class)
                .setParameter("item", item)
                .setMaxResults(1)
                .getResultStream().findFirst().orElse(null);

        if (routeRevision == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal addSumResourceTime = em
                .createQuery("""
                        select coalesce(sum(e.resourceTime),0) from Step e
                        join e.mlmNode mlmNode
                        where e.routeRevision = :routeRevision
                        """, BigDecimal.class)
                .setParameter("routeRevision", routeRevision)
                .getSingleResult();

        addSumResourceTime = addSumResourceTime.multiply(qty);
        List<Object[]> components = em.createQuery("""
                        SELECT c.componentItem, c.qty FROM Component c
                        WHERE c.routerevision = :routeRevision
                        ORDER BY c.number ASC
                        """, Object[].class)
                .setParameter("routeRevision", routeRevision)
                .getResultList();
        BigDecimal totalComponentTime = BigDecimal.ZERO;

        for (Object[] component : components) {
            Item componentItem = (Item) component[0];
            BigDecimal componentQty = (BigDecimal) component[1];

            totalComponentTime = totalComponentTime.add(
                    getTotalProductionTimeForItem(em, componentItem, componentQty).multiply(qty)
            );
        }

        return addSumResourceTime.add(totalComponentTime);
    }

}
